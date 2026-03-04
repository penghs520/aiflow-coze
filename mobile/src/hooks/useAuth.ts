import { useCallback } from 'react';
import { useNavigation, NavigationProp } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { STORAGE_KEYS } from '../utils/constants';
import { RootStackParamList } from '../navigation/AppNavigator';

export const useAuth = () => {
  const navigation = useNavigation<NavigationProp<RootStackParamList>>();

  // 检查是否已登录
  const checkLogin = useCallback(async (): Promise<boolean> => {
    const token = await AsyncStorage.getItem(STORAGE_KEYS.USER_TOKEN);
    return !!token;
  }, []);

  // 要求登录，未登录则跳转到登录页
  const requireLogin = useCallback(async (): Promise<boolean> => {
    const isLoggedIn = await checkLogin();

    if (!isLoggedIn) {
      navigation.navigate('Login');
      return false;
    }

    return true;
  }, [checkLogin, navigation]);

  return { checkLogin, requireLogin };
};
