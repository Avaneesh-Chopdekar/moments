import { useState } from "react";
import { View, Text } from "react-native";
import { VideoView, useVideoPlayer } from "expo-video";
import { Stack } from "expo-router";

export default function Stream() {
  const [videoId, setVideoId] = useState(
    "5fa0df02-c449-45dd-adb4-32e1be5144b6"
  );
  const apiUrl = process.env.EXPO_PUBLIC_API_URL;
  const videoURL = `${apiUrl}/videos/${videoId}/master.m3u8`;

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
