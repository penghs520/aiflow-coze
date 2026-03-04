import React from 'react';
import { View, StyleSheet, ScrollView, Alert } from 'react-native';
import { Text, Card, Button, Avatar, List, Divider } from 'react-native-paper';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useNavigation, CommonActions } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { RootStackParamList } from '../navigation/AppNavigator';
import { COLORS, STORAGE_KEYS } from '../utils/constants';
import AsyncStorage from '@react-native-async-storage/async-storage';

type ProfileScreenNavigationProp = StackNavigationProp<RootStackParamList>;

const ProfileScreen = () => {
  const navigation = useNavigation<ProfileScreenNavigationProp>();

  // 模拟用户数据
  const user = {
    nickname: 'AI创作者',
    phone: '138****8888',
    pointsBalance: 28500,
    avatarUrl: 'https://picsum.photos/100/100',
  };

  // 退出登录
  const handleLogout = async () => {
    console.log('=== 退出登录按钮被点击 ===');

    try {
      console.log('开始清除 token...');
      // 清除本地存储的 token
      await AsyncStorage.removeItem(STORAGE_KEYS.USER_TOKEN);
      console.log('USER_TOKEN 已清除');
      await AsyncStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
      console.log('REFRESH_TOKEN 已清除');

      console.log('准备重置导航栈并跳转到登录页面...');
      // 重置导航栈，清除所有历史记录
      navigation.dispatch(
        CommonActions.reset({
          index: 0,
          routes: [{ name: 'Login' }],
        })
      );
      console.log('导航栈已重置');
    } catch (error) {
      console.error('退出登录失败:', error);
      Alert.alert('错误', '退出登录失败，请稍后重试');
    }
  };

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <ScrollView style={styles.scrollView}>
      <View style={styles.userCard}>
        <View style={styles.userInfo}>
          <Avatar.Image source={{ uri: user.avatarUrl }} size={64} />
          <View style={styles.userDetails}>
            <Text style={styles.nickname}>{user.nickname}</Text>
            <Text style={styles.phone}>{user.phone}</Text>
          </View>
        </View>
        <View style={styles.pointsContainer}>
          <View style={styles.pointsItem}>
            <Text style={styles.pointsValue}>{user.pointsBalance}</Text>
            <Text style={styles.pointsLabel}>资源点</Text>
          </View>
          <Button
            mode="contained"
            style={styles.rechargeButton}
            labelStyle={styles.rechargeButtonLabel}
            buttonColor="#FFA726"
            textColor="#FFFFFF"
            onPress={() => navigation.navigate('Recharge')}
          >
            充值
          </Button>
        </View>
      </View>

      <List.Section style={styles.section}>
        <Text style={styles.sectionTitle}>我的服务</Text>
        <Card style={styles.menuCard}>
          <List.Item
            title="我的收藏"
            titleStyle={styles.menuItemTitle}
            left={props => <List.Icon {...props} icon="heart" color="#FF6B9D" />}
            right={props => <List.Icon {...props} icon="chevron-right" color={COLORS.textSecondary} />}
          />
          <Divider />
          <List.Item
            title="历史任务"
            titleStyle={styles.menuItemTitle}
            left={props => <List.Icon {...props} icon="history" color="#42A5F5" />}
            right={props => <List.Icon {...props} icon="chevron-right" color={COLORS.textSecondary} />}
          />
          <Divider />
          <List.Item
            title="资源点记录"
            titleStyle={styles.menuItemTitle}
            left={props => <List.Icon {...props} icon="cash" color="#66BB6A" />}
            right={props => <List.Icon {...props} icon="chevron-right" color={COLORS.textSecondary} />}
          />
        </Card>
      </List.Section>

      <List.Section style={styles.section}>
        <Text style={styles.sectionTitle}>设置</Text>
        <Card style={styles.menuCard}>
          <List.Item
            title="通知设置"
            titleStyle={styles.menuItemTitle}
            left={props => <List.Icon {...props} icon="bell" color="#FFA726" />}
            right={props => <List.Icon {...props} icon="chevron-right" color={COLORS.textSecondary} />}
          />
          <Divider />
          <List.Item
            title="主题设置"
            titleStyle={styles.menuItemTitle}
            left={props => <List.Icon {...props} icon="theme-light-dark" color="#AB47BC" />}
            right={props => <List.Icon {...props} icon="chevron-right" color={COLORS.textSecondary} />}
          />
          <Divider />
          <List.Item
            title="清除缓存"
            titleStyle={styles.menuItemTitle}
            left={props => <List.Icon {...props} icon="cached" color="#26C6DA" />}
            right={props => <List.Icon {...props} icon="chevron-right" color={COLORS.textSecondary} />}
          />
          <Divider />
          <List.Item
            title="关于我们"
            titleStyle={styles.menuItemTitle}
            left={props => <List.Icon {...props} icon="information" color="#78909C" />}
            right={props => <List.Icon {...props} icon="chevron-right" color={COLORS.textSecondary} />}
          />
        </Card>
      </List.Section>

      <Button
        mode="outlined"
        style={styles.logoutButton}
        labelStyle={styles.logoutButtonLabel}
        textColor="#FF5252"
        onPress={handleLogout}
      >
        退出登录
      </Button>
    </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  scrollView: {
    flex: 1,
  },
  userCard: {
    marginHorizontal: 16,
    marginTop: 16,
    marginBottom: 24,
    padding: 20,
    backgroundColor: COLORS.surface,
    borderRadius: 16,
  },
  userInfo: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 20,
  },
  userDetails: {
    marginLeft: 16,
    flex: 1,
  },
  nickname: {
    fontSize: 20,
    fontWeight: 'bold',
    color: COLORS.text,
    marginBottom: 4,
  },
  phone: {
    fontSize: 14,
    color: COLORS.textSecondary,
  },
  pointsContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingTop: 16,
    borderTopWidth: 1,
    borderTopColor: COLORS.border,
  },
  pointsItem: {
    flexDirection: 'row',
    alignItems: 'baseline',
    gap: 8,
  },
  pointsValue: {
    fontSize: 28,
    fontWeight: 'bold',
    color: COLORS.text,
  },
  pointsLabel: {
    fontSize: 14,
    color: COLORS.textSecondary,
  },
  rechargeButton: {
    borderRadius: 20,
    paddingHorizontal: 24,
  },
  rechargeButtonLabel: {
    fontSize: 14,
    fontWeight: '600',
  },
  section: {
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: COLORS.text,
    marginLeft: 16,
    marginBottom: 12,
  },
  menuCard: {
    marginHorizontal: 16,
    backgroundColor: COLORS.surface,
    elevation: 3,
    borderRadius: 12,
    overflow: 'hidden',
  },
  menuItemTitle: {
    color: COLORS.text,
    fontSize: 15,
  },
  logoutButton: {
    margin: 16,
    marginBottom: 32,
    borderColor: '#FF5252',
    borderRadius: 8,
    paddingVertical: 8,
  },
  logoutButtonLabel: {
    fontSize: 15,
    fontWeight: '600',
  },
});

export default ProfileScreen;