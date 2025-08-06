import { StatusBar } from "expo-status-bar";
import { Stack } from "expo-router";
import "./global.css";

export default function RootLayout() {
  return (
    <>
      <StatusBar style="dark" />
      <Stack />
    </>
  );
}
