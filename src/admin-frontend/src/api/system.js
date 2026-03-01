import request from './request'

// 管理员管理
export const getAdminList = (params) => {
  return request({
    url: '/admin/v1/admins',
    method: 'get',
    params
  })
}

export const getAdminById = (id) => {
  return request({
    url: `/admin/v1/admins/${id}`,
    method: 'get'
  })
}

export const createAdmin = (data) => {
  return request({
    url: '/admin/v1/admins',
    method: 'post',
    data
  })
}

export const updateAdmin = (id, data) => {
  return request({
    url: `/admin/v1/admins/${id}`,
    method: 'put',
    data
  })
}

export const deleteAdmin = (id) => {
  return request({
    url: `/admin/v1/admins/${id}`,
    method: 'delete'
  })
}

export const resetPassword = (id, newPassword) => {
  return request({
    url: `/admin/v1/admins/${id}/reset-password`,
    method: 'put',
    data: { newPassword }
  })
}

export const enableAdmin = (id) => {
  return request({
    url: `/admin/v1/admins/${id}/enable`,
    method: 'put'
  })
}

export const disableAdmin = (id) => {
  return request({
    url: `/admin/v1/admins/${id}/disable`,
    method: 'put'
  })
}

// 角色管理
export const getRoleList = (params) => {
  return request({
    url: '/admin/v1/roles',
    method: 'get',
    params
  })
}

export const getActiveRoles = () => {
  return request({
    url: '/admin/v1/roles/active',
    method: 'get'
  })
}

export const getRoleById = (id) => {
  return request({
    url: `/admin/v1/roles/${id}`,
    method: 'get'
  })
}

export const createRole = (data) => {
  return request({
    url: '/admin/v1/roles',
    method: 'post',
    data
  })
}

export const updateRole = (id, data) => {
  return request({
    url: `/admin/v1/roles/${id}`,
    method: 'put',
    data
  })
}

export const deleteRole = (id) => {
  return request({
    url: `/admin/v1/roles/${id}`,
    method: 'delete'
  })
}

export const enableRole = (id) => {
  return request({
    url: `/admin/v1/roles/${id}/enable`,
    method: 'put'
  })
}

export const disableRole = (id) => {
  return request({
    url: `/admin/v1/roles/${id}/disable`,
    method: 'put'
  })
}

export const getRolePermissions = (id) => {
  return request({
    url: `/admin/v1/roles/${id}/permissions`,
    method: 'get'
  })
}

export const assignPermissions = (id, permissionIds) => {
  return request({
    url: `/admin/v1/roles/${id}/permissions`,
    method: 'put',
    data: permissionIds
  })
}

// 权限管理
export const getPermissionTree = () => {
  return request({
    url: '/admin/v1/permissions/tree',
    method: 'get'
  })
}

export const getPermissionList = () => {
  return request({
    url: '/admin/v1/permissions',
    method: 'get'
  })
}

// 系统配置
export const getConfigList = (params) => {
  return request({
    url: '/admin/v1/configs',
    method: 'get',
    params
  })
}

export const getConfigByKey = (key) => {
  return request({
    url: `/admin/v1/configs/${key}`,
    method: 'get'
  })
}

export const createConfig = (data) => {
  return request({
    url: '/admin/v1/configs',
    method: 'post',
    data
  })
}

export const updateConfig = (key, data) => {
  return request({
    url: `/admin/v1/configs/${key}`,
    method: 'put',
    data
  })
}

export const deleteConfig = (key) => {
  return request({
    url: `/admin/v1/configs/${key}`,
    method: 'delete'
  })
}

// 操作日志
export const getLogList = (params) => {
  return request({
    url: '/admin/v1/logs',
    method: 'get',
    params
  })
}
