const api = require('../../utils/api')

Page({
  data: { list: [] },
  onShow() { this.loadData() },
  async loadData() {
    const res = await api.getNotices({ page: 1, size: 50 })
    this.setData({ list: res.data.records })
  },
  async markRead(e) {
    const { id, index } = e.currentTarget.dataset
    if (!this.data.list[index].isRead) {
      await api.markRead(id)
      this.setData({ [`list[${index}].isRead`]: 1 })
    }
  }
})
