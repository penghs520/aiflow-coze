import request from './request'

export const getUserList = (params) => {
  return request({
    url: '/admin/v1/users',
    method: 'get',
    params
  })
}

export const searchUsers = (keyword) => {
  return request({
    url: '/admin/v1/users/search',
    method: 'get',
    params: { keyword }
  })
}

export const getUserById = (id) => {
  return request({
    url: `/admin/v1/users/${id}`,
    method: 'get'
  })
}

export const disableUser = (id) => {
  return request({
    url: `/admin/v1/users/${id}/disable`,
    method: 'put'
  })
}

export const enableUser = (id) => {
  return request({
    url: `/admin/v1/users/${id}/enable`,
    method: 'put'
  })
}

export const adjustPoints = (id, points, reason) => {
  return request({
    url: `/admin/v1/users/${id}/points`,
    method: 'put',
    data: { points, reason }
  })
}

export const batchDisable = (ids) => {
  return request({
    url: '/admin/v1/users/batch/disable',
    method: 'put',
    data: ids
  })
}

export const batchEnable = (ids) => {
  return request({
    url: '/admin/v1/users/batch/enable',
    method: 'put',
    data: ids
  })
}
