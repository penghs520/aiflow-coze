import api from './api';

interface RechargePackage {
  id: string;
  name: string;
  price: number;
  points: number;
  description?: string;
}

interface CreateOrderRequest {
  packageId: string;
  amount: number;
  points: number;
}

interface Order {
  id: string;
  userId: string;
  orderType: number;
  packageId: string;
  amount: number;
  points: number;
  status: number;
  paymentChannel?: string;
  paymentTransactionId?: string;
  paidAt?: string;
  createdAt: string;
  updatedAt: string;
}

class PaymentApi {
  // 获取充值套餐
  async getPackages(): Promise<RechargePackage[]> {
    return api.get('/payment/packages');
  }

  // 创建订单
  async createOrder(data: CreateOrderRequest): Promise<Order> {
    return api.post('/payment/orders', data);
  }

  // 获取订单详情
  async getOrderDetail(id: string): Promise<Order> {
    return api.get(`/payment/orders/${id}`);
  }
}

export const paymentApi = new PaymentApi();
export default paymentApi;