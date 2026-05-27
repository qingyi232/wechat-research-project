package com.research.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableScheduling
public class BackupConfig {

    private static final Logger log = LoggerFactory.getLogger(BackupConfig.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${backup.path:./backups/}")
    private String backupPath;

    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledBackup() {
        try {
            String dbName = extractDbName(dbUrl);
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String fileName = String.format("backup_%s_%s.sql", dbName, date);

            File dir = new File(backupPath);
            if (!dir.exists()) dir.mkdirs();

            String command = String.format("mysqldump -u%s -p%s %s > %s%s",
                    dbUsername, dbPassword, dbName, backupPath, fileName);

            Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
            log.info("数据库备份任务执行完成: {}", fileName);

            cleanOldBackups(dir);
        } catch (Exception e) {
            log.error("数据库备份失败", e);
        }
    }

    private String extractDbName(String url) {
        String sub = url.substring(url.lastIndexOf("/") + 1);
        int idx = sub.indexOf("?");
        return idx > 0 ? sub.substring(0, idx) : sub;
    }

    private void cleanOldBackups(File dir) {
        File[] files = dir.listFiles((d, name) -> name.startsWith("backup_") && name.endsWith(".sql"));
        if (files == null) return;
        long threshold = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000;
        for (File f : files) {
            if (f.lastModified() < threshold) {
                if (f.delete()) {
                    log.info("已清理过期备份: {}", f.getName());
                }
            }
        }
    }
}
