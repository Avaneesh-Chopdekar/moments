import { useEffect, useState, useRef } from "react";
import { Button, Text, View, TextInput } from "react-native";
import * as ImagePicker from "expo-image-picker";
import { useVideoPlayer, VideoView } from "expo-video";
import { Stack } from "expo-router";

export default function Index() {
  const [video, setVideo] = useState<string | null>(null);
  const [title, setTitle] = useState<string>("");
  const [description, setDescription] = useState<string>("");
  const descriptionInputRef = useRef<TextInput | null>(null);

  const player = useVideoPlayer(video, (player) => {
    player.loop = true;
    player.showNowPlayingNotification = true;
  });

  const pickVideo = async () => {
    let result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ["videos"],
      allowsEditing: true,
      aspect: [16, 9],
      quality: 1,
    });

    console.log(result);

    if (!result.canceled) {
      setVideo(result.assets[0].uri);
    }
  };

  useEffect(() => {
    if (player && !player.playing) {
      player.play();
    }
  }, [player]);

  const submitDisabled =
    title.length === 0 || description.length === 0 || video === null;

  const handleSubmit = async () => {
    if (submitDisabled) {
      return;
    }
    console.log(title, description, video);
  };

  return (
    <>
      <Stack.Screen
        options={{
          headerTitle: "Moments",
          headerTitleAlign: "center",
        }}
      />
      <View className="flex-1 items-center gap-8 px-8 pt-12 bg-white">
        <Text className="text-xl font-bold text-blue-500">Upload Video</Text>

        <TextInput
          placeholder="Title"
          value={title}
          onChangeText={setTitle}
          className="w-full border border-gray-300 p-2"
          placeholderTextColor={"gray"}
          maxLength={50}
          onSubmitEditing={() => descriptionInputRef.current?.focus()}
          enterKeyHint="next"
        />
        <TextInput
          placeholder="Description"
          value={description}
          onChangeText={setDescription}
          multiline
          className="w-full border border-gray-300 p-2"
          placeholderTextColor={"gray"}
          maxLength={200}
          ref={descriptionInputRef}
        />
        <Button title="Select video from gallery" onPress={pickVideo} />
        {video && (
          <>
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
          </>
        )}
        <Button
          title="Upload Video"
          onPress={handleSubmit}
          disabled={submitDisabled}
        />
      </View>
    </>
  );
}
