import React from 'react';
import { View, StyleSheet, ScrollView } from 'react-native';
import { Text, Card, Button, Avatar, List, Divider } from 'react-native-paper';
import { useNavigation } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { RootStackParamList } from '../navigation/AppNavigator';
import { COLORS } from '../utils/constants';

type ProfileScreenNavigationProp = StackNavigationProp<RootStackParamList, 'Main'>;

const ProfileScreen = () => {
  const navigation = useNavigation<ProfileScreenNavigationProp>();

  // 模拟用户数据
  const user = {
    nickname: 'AI创作者',
    phone: '138****8888',
    pointsBalance: 28500,
    avatarUrl: 'https://picsum.photos/100/100',
  };

  return (
    <ScrollView style={styles.container}>
      <Card style={styles.userCard}>
        <Card.Content>
          <View style={styles.userInfo}>
            <Avatar.Image source={{ uri: user.avatarUrl }} size={80} />
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
            <Button mode="contained" style={styles.rechargeButton} onPress={() => navigation.navigate('Recharge')}>
              充值
            </Button>
          </View>
        </Card.Content>
      </Card>

      <List.Section style={styles.section}>
        <Text style={styles.sectionTitle}>我的服务</Text>
        <Card style={styles.menuCard}>
          <List.Item
            title="我的收藏"
            left={props => <List.Icon {...props} icon="heart" />}
            right={props => <List.Icon {...props} icon="chevron-right" />}
          />
          <Divider />
          <List.Item
            title="历史任务"
            left={props => <List.Icon {...props} icon="history" />}
            right={props => <List.Icon {...props} icon="chevron-right" />}
          />
          <Divider />
          <List.Item
            title="资源点记录"
            left={props => <List.Icon {...props} icon="cash" />}
            right={props => <List.Icon {...props} icon="chevron-right" />}
          />
        </Card>
      </List.Section>

      <List.Section style={styles.section}>
        <Text style={styles.sectionTitle}>设置</Text>
        <Card style={styles.menuCard}>
          <List.Item
            title="通知设置"
            left={props => <List.Icon {...props} icon="bell" />}
            right={props => <List.Icon {...props} icon="chevron-right" />}
          />
          <Divider />
          <List.Item
            title="主题设置"
            left={props => <List.Icon {...props} icon="theme-light-dark" />}
            right={props => <List.Icon {...props} icon="chevron-right" />}
          />
          <Divider />
          <List.Item
            title="清除缓存"
            left={props => <List.Icon {...props} icon="cached" />}
            right={props => <List.Icon {...props} icon="chevron-right" />}
          />
          <Divider />
          <List.Item
            title="关于我们"
            left={props => <List.Icon {...props} icon="information" />}
            right={props => <List.Icon {...props} icon="chevron-right" />}
          />
        </Card>
      </List.Section>

      <Button mode="outlined" style={styles.logoutButton}>
        退出登录
      </Button>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  userCard: {
    margin: 16,
    backgroundColor: COLORS.surface,
    elevation: 3,
    borderRadius: 16,
  },
  userInfo: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 24,
  },
  userDetails: {
    marginLeft: 20,
  },
  nickname: {
    fontSize: 20,
    fontWeight: 'bold',
    color: COLORS.text,
    marginBottom: 6,
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
    alignItems: 'center',
  },
  pointsValue: {
    fontSize: 28,
    fontWeight: 'bold',
    color: COLORS.primary,
  },
  pointsLabel: {
    fontSize: 13,
    color: COLORS.textSecondary,
    marginTop: 4,
  },
  rechargeButton: {
    paddingHorizontal: 36,
    borderRadius: 8,
    paddingVertical: 6,
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
  logoutButton: {
    margin: 16,
    marginBottom: 32,
    borderColor: COLORS.error,
    borderRadius: 8,
    paddingVertical: 8,
  },
});

export default ProfileScreen;