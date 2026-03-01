import request from './request'

export const getWorkflowList = (params) => {
  return request({
    url: '/admin/v1/workflows',
    method: 'get',
    params
  })
}

export const searchWorkflows = (keyword, params) => {
  return request({
    url: '/admin/v1/workflows/search',
    method: 'get',
    params: { keyword, ...params }
  })
}

export const getWorkflowById = (id) => {
  return request({
    url: `/admin/v1/workflows/${id}`,
    method: 'get'
  })
}

export const createWorkflow = (data) => {
  return request({
    url: '/admin/v1/workflows',
    method: 'post',
    data
  })
}

export const updateWorkflow = (id, data) => {
  return request({
    url: `/admin/v1/workflows/${id}`,
    method: 'put',
    data
  })
}

export const deleteWorkflow = (id) => {
  return request({
    url: `/admin/v1/workflows/${id}`,
    method: 'delete'
  })
}

export const publishWorkflow = (id) => {
  return request({
    url: `/admin/v1/workflows/${id}/publish`,
    method: 'put'
  })
}

export const unpublishWorkflow = (id) => {
  return request({
    url: `/admin/v1/workflows/${id}/unpublish`,
    method: 'put'
  })
}

export const batchPublish = (ids) => {
  return request({
    url: '/admin/v1/workflows/batch/publish',
    method: 'put',
    data: ids
  })
}

export const batchUnpublish = (ids) => {
  return request({
    url: '/admin/v1/workflows/batch/unpublish',
    method: 'put',
    data: ids
  })
}
