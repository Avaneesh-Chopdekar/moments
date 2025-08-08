import { Stack, useRouter } from "expo-router";
import { Button } from "react-native";

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
    </>
  );
}
