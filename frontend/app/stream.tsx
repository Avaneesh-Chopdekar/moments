import { useState } from "react";
import { View, Text } from "react-native";
import { VideoView, useVideoPlayer } from "expo-video";
import { Stack } from "expo-router";

export default function Stream() {
  const [videoId, setVideoId] = useState(
    "cefc3391-a28a-4f2f-84fe-942663b972d0"
  );
  const apiUrl = process.env.EXPO_PUBLIC_API_URL;
  const videoURL = `${apiUrl}/videos/stream/range/${videoId}`;

  const player = useVideoPlayer(videoURL, (player) => {
    player.loop = true;
    player.showNowPlayingNotification = true;
  });

  return (
    <>
      <Stack.Screen
        options={{
          headerTitle: "Moments",
          headerTitleAlign: "center",
        }}
      />
      <View>
        <Text className="text-2xl text-center font-bold my-8">
          Playing Video
        </Text>

        <VideoView
          style={{
            width: "100%",
            aspectRatio: 16 / 9,
            borderWidth: 2,
            borderColor: "black",
          }}
          player={player}
          allowsFullscreen
          allowsPictureInPicture
          startsPictureInPictureAutomatically
        />
      </View>
    </>
  );
}
