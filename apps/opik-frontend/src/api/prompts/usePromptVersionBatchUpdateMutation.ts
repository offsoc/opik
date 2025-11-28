import { useMutation, useQueryClient } from "@tanstack/react-query";
import api, { PROMPTS_REST_ENDPOINT } from "@/api/api";
import { useToast } from "@/components/ui/use-toast";

type UsePromptVersionBatchUpdateMutationParams = {
  versionIds: string[];
  tags: string[];
  mergeTags: boolean;
};

const batchUpdatePromptVersions = async ({
  versionIds,
  tags,
  mergeTags,
}: UsePromptVersionBatchUpdateMutationParams) => {
  await api.patch(`${PROMPTS_REST_ENDPOINT}versions/batch`, {
    ids: versionIds,
    update: {
      tags,
    },
    merge_tags: mergeTags,
  });
};

export default function usePromptVersionBatchUpdateMutation() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: batchUpdatePromptVersions,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["prompt-versions"] });
      queryClient.invalidateQueries({ queryKey: ["prompt-version"] });
      queryClient.invalidateQueries({ queryKey: ["prompt"] });
    },
    onError: (error) => {
      toast({
        variant: "destructive",
        description: error.message || "Failed to update tags",
      });
    },
  });
}

