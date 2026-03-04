import React, { useState } from 'react';
import {
  View,
  StyleSheet,
  Image,
  Platform,
  ScrollView,
  Alert,
  Keyboard,
  TouchableOpacity,
} from 'react-native';
import { Text, TextInput, Button, Card } from 'react-native-paper';
import { useNavigation } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { RootStackParamList } from '../navigation/AppNavigator';
import { COLORS, STORAGE_KEYS } from '../utils/constants';
import { userApi } from '../services/api';
import AsyncStorage from '@react-native-async-storage/async-storage';

type LoginScreenNavigationProp = StackNavigationProp<RootStackParamList, 'Login'>;

// 自定义主题覆盖，确保输入框文字颜色正确
const textInputTheme = {
  colors: {
    primary: COLORS.primary,
    onSurfaceVariant: COLORS.textSecondary,
    onSurface: COLORS.text,
    outline: COLORS.border,
  },
};

const LoginScreen = () => {
  const navigation = useNavigation<LoginScreenNavigationProp>();
  const [phone, setPhone] = useState('');
  const [code, setCode] = useState('');
  const [countdown, setCountdown] = useState(0);
  const [loading, setLoading] = useState(false);
  const [sendingCode, setSendingCode] = useState(false);

  const handleSendCode = async () => {
    if (!phone || phone.length !== 11) {
      Alert.alert('提示', '请输入正确的手机号');
      return;
    }

    try {
      setSendingCode(true);
      await userApi.sendSmsCode({ phone });

      setCountdown(60);
      const timer = setInterval(() => {
        setCountdown((prev) => {
          if (prev <= 1) {
            clearInterval(timer);
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    } catch (error: any) {
      Alert.alert('发送失败', error.message || '请稍后重试');
    } finally {
      setSendingCode(false);
    }
  };

  const handleLogin = async () => {
    if (!phone || phone.length !== 11) {
      Alert.alert('提示', '请输入正确的手机号');
      return;
    }

    if (!code || code.length !== 6) {
      Alert.alert('提示', '请输入6位验证码');
      return;
    }

    // 关闭键盘
    Keyboard.dismiss();

    try {
      setLoading(true);
      const response = await userApi.login({ phone, code });

      await AsyncStorage.setItem(STORAGE_KEYS.USER_TOKEN, response.token);
      console.log('登录成功，token:', response.token);

      // 直接跳转到主页，不显示弹窗
      navigation.replace('Main');
    } catch (error: any) {
      Alert.alert('登录失败', error.message || '请检查手机号和验证码');
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        keyboardShouldPersistTaps="handled"
      >
          <View style={styles.logoContainer}>
            <Text style={styles.appName}>即刻创作</Text>
            <Text style={styles.appSubtitle}>AI视频制作，一键生成</Text>
          </View>

          <Card style={styles.loginCard}>
            <Card.Content style={styles.cardContent}>
              {/* 手机号输入框 */}
              <View style={styles.inputContainer}>
                <Text style={styles.inputLabel}>手机号</Text>
                <TextInput
                  value={phone}
                  onChangeText={setPhone}
                  style={styles.input}
                  keyboardType="phone-pad"
                  maxLength={11}
                  placeholder="请输入11位手机号"
                  placeholderTextColor={COLORS.textSecondary}
                  textColor={COLORS.text}
                  theme={textInputTheme}
                  mode="outlined"
                  outlineColor={COLORS.border}
                  activeOutlineColor={COLORS.primary}
                />
              </View>

              {/* 验证码输入框 */}
              <View style={styles.inputContainer}>
                <Text style={styles.inputLabel}>验证码</Text>
                <View style={styles.codeContainer}>
                  <TextInput
                    value={code}
                    onChangeText={setCode}
                    style={styles.codeInput}
                    keyboardType="number-pad"
                    maxLength={6}
                    placeholder="请输入6位验证码"
                    placeholderTextColor={COLORS.textSecondary}
                    textColor={COLORS.text}
                    theme={textInputTheme}
                    mode="outlined"
                    outlineColor={COLORS.border}
                    activeOutlineColor={COLORS.primary}
                  />
                  <Button
                    mode="outlined"
                    style={styles.codeButton}
                    labelStyle={styles.codeButtonLabel}
                    onPress={handleSendCode}
                    disabled={countdown > 0 || sendingCode}
                    loading={sendingCode}
                    textColor={countdown > 0 ? COLORS.textSecondary : COLORS.primary}
                  >
                    {countdown > 0 ? `${countdown}s后重试` : '获取验证码'}
                  </Button>
                </View>
              </View>

              {/* 登录按钮 */}
              <Button
                mode="contained"
                style={styles.loginButton}
                labelStyle={styles.loginButtonLabel}
                onPress={handleLogin}
                loading={loading}
                disabled={loading}
                buttonColor={COLORS.primary}
                textColor={COLORS.text}
              >
                登录
              </Button>

              {/* 用户协议 */}
              <Text style={styles.termsText}>
                登录即表示同意<Text style={styles.termsLink}>《用户协议》</Text>和<Text style={styles.termsLink}>《隐私政策》</Text>
              </Text>
            </Card.Content>
          </Card>
        </ScrollView>
      </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  scrollContent: {
    flexGrow: 1,
    justifyContent: 'center',
    padding: 24,
  },
  logoContainer: {
    alignItems: 'center',
    marginBottom: 48,
  },
  appName: {
    fontSize: 32,
    fontWeight: 'bold',
    color: COLORS.text,
    marginBottom: 8,
  },
  appSubtitle: {
    fontSize: 14,
    color: COLORS.textSecondary,
    letterSpacing: 0.5,
  },
  loginCard: {
    backgroundColor: COLORS.surface,
    borderRadius: 20,
    elevation: 4,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.3,
    shadowRadius: 4,
  },
  cardContent: {
    padding: 28,
  },
  inputContainer: {
    marginBottom: 24,
  },
  inputLabel: {
    fontSize: 14,
    fontWeight: '600',
    color: COLORS.text,
    marginBottom: 10,
  },
  input: {
    backgroundColor: COLORS.background,
    fontSize: 16,
  },
  codeContainer: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    gap: 10,
  },
  codeInput: {
    flex: 1,
    backgroundColor: COLORS.background,
    fontSize: 16,
  },
  codeButton: {
    height: 56,
    justifyContent: 'center',
    borderColor: COLORS.primary,
    borderRadius: 10,
    minWidth: 110,
  },
  codeButtonLabel: {
    fontSize: 13,
    fontWeight: '500',
  },
  loginButton: {
    height: 52,
    justifyContent: 'center',
    borderRadius: 12,
    marginTop: 12,
    marginBottom: 20,
    elevation: 2,
  },
  loginButtonLabel: {
    fontSize: 17,
    fontWeight: 'bold',
    letterSpacing: 0.5,
  },
  termsText: {
    fontSize: 12,
    color: COLORS.textSecondary,
    textAlign: 'center',
    lineHeight: 20,
  },
  termsLink: {
    color: COLORS.primary,
    fontWeight: '500',
  },
});

export default LoginScreen;
