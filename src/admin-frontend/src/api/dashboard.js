import request from './request'

export const getDashboardData = () => {
  return request({
    url: '/admin/v1/dashboard',
    method: 'get'
  })
}
