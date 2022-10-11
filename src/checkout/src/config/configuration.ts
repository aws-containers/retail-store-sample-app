export default () => ({
  endpoints: {
    orders: process.env.ENDPOINTS_ORDERS || ''
  },
  redis: {
    url: process.env.REDIS_URL || '',
    reader: {
      url: process.env.REDIS_READER_URL || '',
    }
  }
});