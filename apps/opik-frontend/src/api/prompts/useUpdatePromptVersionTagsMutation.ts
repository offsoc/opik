import { useMutation, useQueryClient } from "@tanstack/react-query";
import { AxiosError } from "axios";
import api, { PROMPTS_REST_ENDPOINT } from "@/api/api";
import { useToast } from "@/components/ui/use-toast";

type UseUpdatePromptVersionTagsMutationParams = {
  versionId: string;
  tags: string[];
};

const useUpdatePromptVersionTagsMutation = () => {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: async ({
      versionId,
      tags,
    }: UseUpdatePromptVersionTagsMutationParams) => {
      const { data } = await api.patch(
        `${PROMPTS_REST_ENDPOINT}versions/${versionId}/tags`,
        {
          tags,
        },
      );

      return data;
    },
    onError: (error: AxiosError) => {
      const message =
        // @ts-expect-error error response shape not typed
        error?.response?.data?.message || "Failed to update tags";

      toast({
        title: "Error",
        description: message,
        variant: "destructive",
      });
    },
    onSuccess: () => {
      // Invalidate all prompt version queries to refresh the data
      queryClient.invalidateQueries({ queryKey: ["prompt-versions"] });
      queryClient.invalidateQueries({ queryKey: ["prompt-version"] });
    },
  });
};

export default useUpdatePromptVersionTagsMutation;

