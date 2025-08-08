import { Stack, useRouter } from "expo-router";
import { View, Button } from "react-native";

export default function Index() {
  const router = useRouter();

  return (
    <>
      <Stack.Screen
        options={{
          headerTitle: "Moments",
          headerTitleAlign: "center",
          headerRight() {
            return (
              <Button
                title="Post"
                onPress={() => {
                  router.navigate("/post");
                }}
              />
            );
          },
        }}
      />

      <View className="flex-1 justify-center items-center">
        <Button
          title="Stream Video"
          onPress={() => {
            router.navigate("/stream");
          }}
        />
      </View>
    </>
  );
}
