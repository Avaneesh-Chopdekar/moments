import { useEffect, useState, useRef } from "react";
import { Button, Text, View, TextInput } from "react-native";
import * as ImagePicker from "expo-image-picker";
import * as FileSystem from "expo-file-system";
import * as Progress from "react-native-progress";
import { useVideoPlayer, VideoView } from "expo-video";
import { Stack } from "expo-router";
import axios from "axios";
import Toast from "react-native-toast-message";

export default function Index() {
  const [video, setVideo] = useState<string | null>(null);
  const [title, setTitle] = useState<string>("");
  const [description, setDescription] = useState<string>("");
  const descriptionInputRef = useRef<TextInput | null>(null);

  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [uploadMessage, setUploadMessage] = useState("");

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
    title.length === 0 ||
    description.length === 0 ||
    video === null ||
    uploading;

  const handleSubmit = async () => {
    if (submitDisabled) {
      return;
    }

    setUploading(true);

    const fileInfo = await FileSystem.getInfoAsync(video);
    if (!fileInfo.exists) {
      throw new Error("File not found");
    }

    const fileName = video.split("/").pop();
    const fileType = fileName?.split(".").pop();

    const formData = new FormData();
    formData.append("title", title);
    formData.append("description", description);
    formData.append("file", {
      uri: video,
      name: fileName || `video.${fileType}`,
      type: `video/${fileType}`, // e.g., video/mp4
    } as any);

    const apiUrl = process.env.EXPO_PUBLIC_API_URL;

    try {
      const response = await axios.post(`${apiUrl}/videos`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
        onUploadProgress(progressEvent) {
          const progress = Math.min(
            100,
            Math.round((progressEvent.loaded / progressEvent.total!) * 100)
          );
          setUploadProgress(progress);
        },
      });
      // console.log("Video uploaded successfully");
      Toast.show({
        type: "success",
        text1: "Video uploaded successfully",
      });
      setTitle("");
      setDescription("");
      setVideo(null);
    } catch (error) {
      Toast.show({
        type: "error",
        text1: "Video uploaded successfully",
      });
      console.error("Video upload failed", error);
    } finally {
      setUploading(false);
    }
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
          title={uploading ? "Uploading..." : "Upload Video"}
          onPress={handleSubmit}
          disabled={submitDisabled}
        />

        {uploading && (
          <Progress.Circle
            size={50}
            progress={uploadProgress}
            showsText
            formatText={() => `${uploadProgress}%`}
          />
        )}
      </View>
    </>
  );
}
