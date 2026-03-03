import React from 'react';
import { StatusBar } from 'expo-status-bar';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { Provider as PaperProvider } from 'react-native-paper';
import { NavigationContainer } from '@react-navigation/native';
import AppNavigator from './src/navigation/AppNavigator';
import { COLORS } from './src/utils/constants';

const theme = {
  colors: {
    primary: COLORS.primary,
    accent: COLORS.secondary,
    background: COLORS.background,
    surface: COLORS.surface,
    text: COLORS.text,
    error: COLORS.error,
    disabled: COLORS.textSecondary,
    placeholder: COLORS.textSecondary,
    backdrop: 'rgba(0, 0, 0, 0.5)',
    elevation: {
      level0: 'transparent',
      level1: 'rgba(0, 0, 0, 0.05)',
      level2: 'rgba(0, 0, 0, 0.08)',
      level3: 'rgba(0, 0, 0, 0.11)',
      level4: 'rgba(0, 0, 0, 0.12)',
      level5: 'rgba(0, 0, 0, 0.14)',
    },
  },
};

export default function App() {
  return (
    <SafeAreaProvider>
      <PaperProvider theme={theme}>
        <NavigationContainer>
          <AppNavigator />
          <StatusBar style="light" />
        </NavigationContainer>
      </PaperProvider>
    </SafeAreaProvider>
  );
}