import request from './request'

export const login = (data) => {
  return request({
    url: '/admin/v1/auth/login',
    method: 'post',
    data
  })
}

export const getAdminInfo = () => {
  return request({
    url: '/admin/v1/auth/me',
    method: 'get'
  })
}

export const changePassword = (oldPassword, newPassword) => {
  return request({
    url: '/admin/v1/auth/password',
    method: 'put',
    params: { oldPassword, newPassword }
  })
}
