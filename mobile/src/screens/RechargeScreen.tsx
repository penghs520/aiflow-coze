import React, { useState } from 'react';
import { View, StyleSheet, ScrollView } from 'react-native';
import { Text, Card, Button, RadioButton, Divider } from 'react-native-paper';
import { useNavigation } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { RootStackParamList } from '../navigation/AppNavigator';
import { COLORS } from '../utils/constants';

type RechargeScreenNavigationProp = StackNavigationProp<RootStackParamList, 'Recharge'>;

const RechargeScreen = () => {
  const navigation = useNavigation<RechargeScreenNavigationProp>();
  const [selectedPackage, setSelectedPackage] = useState('10000');
  const [paymentMethod, setPaymentMethod] = useState('wechat');

  // 充值套餐
  const packages = [
    { id: '10000', points: 10000, price: 30 },
    { id: '30000', points: 30000, price: 80 },
    { id: '50000', points: 50000, price: 120 },
    { id: '100000', points: 100000, price: 200 },
  ];

  const handleRecharge = () => {
    // 模拟充值
    navigation.goBack();
  };

  return (
    <ScrollView style={styles.container}>
      <Card style={styles.packageCard}>
        <Card.Content>
          <Text style={styles.sectionTitle}>选择套餐</Text>
          {packages.map((pkg) => (
            <RadioButton.Group
              key={pkg.id}
              onValueChange={(value) => setSelectedPackage(value)}
              value={selectedPackage}
            >
              <View style={styles.packageItem}>
                <View style={styles.packageInfo}>
                  <Text style={styles.pointsText}>{pkg.points} 资源点</Text>
                  <Text style={styles.priceText}>¥{pkg.price}</Text>
                </View>
                <RadioButton.Android value={pkg.id} />
              </View>
              <Divider />
            </RadioButton.Group>
          ))}
        </Card.Content>
      </Card>

      <Card style={styles.paymentCard}>
        <Card.Content>
          <Text style={styles.sectionTitle}>支付方式</Text>
          <RadioButton.Group
            onValueChange={(value) => setPaymentMethod(value)}
            value={paymentMethod}
          >
            <View style={styles.paymentItem}>
              <Text style={styles.paymentText}>微信支付</Text>
              <RadioButton.Android value="wechat" />
            </View>
            <Divider />
            <View style={styles.paymentItem}>
              <Text style={styles.paymentText}>支付宝</Text>
              <RadioButton.Android value="alipay" />
            </View>
          </RadioButton.Group>
        </Card.Content>
      </Card>

      <Card style={styles.summaryCard}>
        <Card.Content>
          <Text style={styles.sectionTitle}>充值详情</Text>
          <View style={styles.summaryRow}>
            <Text style={styles.summaryLabel}>充值金额</Text>
            <Text style={styles.summaryValue}>
              ¥{packages.find(pkg => pkg.id === selectedPackage)?.price}
            </Text>
          </View>
          <View style={styles.summaryRow}>
            <Text style={styles.summaryLabel}>获得资源点</Text>
            <Text style={styles.summaryValue}>
              {packages.find(pkg => pkg.id === selectedPackage)?.points} 点
            </Text>
          </View>
        </Card.Content>
      </Card>

      <Button mode="contained" style={styles.rechargeButton} onPress={handleRecharge}>
        确认充值
      </Button>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  packageCard: {
    margin: 16,
    backgroundColor: COLORS.surface,
    elevation: 2,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: COLORS.text,
    marginBottom: 12,
  },
  packageItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
  },
  packageInfo: {
    flex: 1,
  },
  pointsText: {
    fontSize: 16,
    color: COLORS.text,
    marginBottom: 4,
  },
  priceText: {
    fontSize: 14,
    color: COLORS.primary,
    fontWeight: '500',
  },
  paymentCard: {
    margin: 16,
    marginTop: 0,
    backgroundColor: COLORS.surface,
    elevation: 2,
  },
  paymentItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
  },
  paymentText: {
    fontSize: 16,
    color: COLORS.text,
  },
  summaryCard: {
    margin: 16,
    marginTop: 0,
    backgroundColor: COLORS.surface,
    elevation: 2,
  },
  summaryRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 8,
  },
  summaryLabel: {
    fontSize: 14,
    color: COLORS.textSecondary,
  },
  summaryValue: {
    fontSize: 14,
    color: COLORS.text,
    fontWeight: '500',
  },
  rechargeButton: {
    margin: 16,
    paddingVertical: 8,
  },
});

export default RechargeScreen;