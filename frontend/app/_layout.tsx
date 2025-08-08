import { StatusBar } from "expo-status-bar";
import { Stack } from "expo-router";
import { DefaultTheme, ThemeProvider } from "@react-navigation/native";
import Toast from "react-native-toast-message";
import "./global.css";

export default function RootLayout() {
  return (
    <ThemeProvider value={DefaultTheme}>
      <StatusBar style="dark" />
      <Stack></Stack>
      <Toast />
    </ThemeProvider>
  );
}
