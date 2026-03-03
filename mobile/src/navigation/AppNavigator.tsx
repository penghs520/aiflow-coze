import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import TabNavigator from './TabNavigator';
import WorkflowDetailScreen from '../screens/WorkflowDetailScreen';
import ParameterInputScreen from '../screens/ParameterInputScreen';
import TaskDetailScreen from '../screens/TaskDetailScreen';
import RechargeScreen from '../screens/RechargeScreen';
import LoginScreen from '../screens/LoginScreen';
import { COLORS } from '../utils/constants';

export type RootStackParamList = {
  Main: undefined;
  WorkflowDetail: { workflowId: string };
  ParameterInput: { workflowId: string };
  TaskDetail: { taskId: string };
  Recharge: undefined;
  Login: undefined;
};

const Stack = createStackNavigator<RootStackParamList>();

const AppNavigator = () => {
  return (
    <Stack.Navigator
      screenOptions={{
        headerStyle: {
          backgroundColor: COLORS.surface,
          elevation: 0,
          shadowOpacity: 0,
          borderBottomWidth: 1,
          borderBottomColor: COLORS.border,
        },
        headerTintColor: COLORS.text,
        headerTitleStyle: {
          fontWeight: 'bold',
        },
        headerBackTitle: '返回',
      }}
    >
      <Stack.Screen name="Main" component={TabNavigator} options={{ headerShown: false }} />
      <Stack.Screen name="WorkflowDetail" component={WorkflowDetailScreen} options={{ title: '工作流详情' }} />
      <Stack.Screen name="ParameterInput" component={ParameterInputScreen} options={{ title: '参数输入' }} />
      <Stack.Screen name="TaskDetail" component={TaskDetailScreen} options={{ title: '任务详情' }} />
      <Stack.Screen name="Recharge" component={RechargeScreen} options={{ title: '充值' }} />
      <Stack.Screen name="Login" component={LoginScreen} options={{ title: '登录', headerShown: false }} />
    </Stack.Navigator>
  );
};

export default AppNavigator;