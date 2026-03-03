import AsyncStorage from '@react-native-async-storage/async-storage';

class StorageService {
  // 存储数据
  async set(key: string, value: any): Promise<void> {
    try {
      const jsonValue = JSON.stringify(value);
      await AsyncStorage.setItem(key, jsonValue);
    } catch (error) {
      console.error('Storage set error:', error);
    }
  }

  // 获取数据
  async get<T>(key: string): Promise<T | null> {
    try {
      const jsonValue = await AsyncStorage.getItem(key);
      return jsonValue != null ? JSON.parse(jsonValue) : null;
    } catch (error) {
      console.error('Storage get error:', error);
      return null;
    }
  }

  // 删除数据
  async remove(key: string): Promise<void> {
    try {
      await AsyncStorage.removeItem(key);
    } catch (error) {
      console.error('Storage remove error:', error);
    }
  }

  // 清空所有数据
  async clear(): Promise<void> {
    try {
      await AsyncStorage.clear();
    } catch (error) {
      console.error('Storage clear error:', error);
    }
  }

  // 获取所有键
  async getAllKeys(): Promise<string[]> {
    try {
      return await AsyncStorage.getAllKeys();
    } catch (error) {
      console.error('Storage getAllKeys error:', error);
      return [];
    }
  }

  // 批量获取
  async multiGet(keys: string[]): Promise<{ [key: string]: any }> {
    try {
      const results = await AsyncStorage.multiGet(keys);
      const data: { [key: string]: any } = {};
      results.forEach(([key, value]) => {
        if (value) {
          try {
            data[key] = JSON.parse(value);
          } catch {
            data[key] = value;
          }
        }
      });
      return data;
    } catch (error) {
      console.error('Storage multiGet error:', error);
      return {};
    }
  }

  // 批量存储
  async multiSet(data: { [key: string]: any }): Promise<void> {
    try {
      const pairs = Object.entries(data).map(([key, value]) => [
        key,
        JSON.stringify(value),
      ]);
      await AsyncStorage.multiSet(pairs);
    } catch (error) {
      console.error('Storage multiSet error:', error);
    }
  }

  // 批量删除
  async multiRemove(keys: string[]): Promise<void> {
    try {
      await AsyncStorage.multiRemove(keys);
    } catch (error) {
      console.error('Storage multiRemove error:', error);
    }
  }
}

export const storage = new StorageService();
export default storage;