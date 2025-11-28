import { Copy, GitCommitVertical, Undo2 } from "lucide-react";
import copy from "clipboard-copy";
import { cn } from "@/lib/utils";

import { formatDate } from "@/lib/date";
import React, { useState } from "react";
import TooltipWrapper from "@/components/shared/TooltipWrapper/TooltipWrapper";
import { Button } from "@/components/ui/button";
import { useToast } from "@/components/ui/use-toast";
import { PromptVersion } from "@/types/prompts";
import ColoredTag from "@/components/shared/ColoredTag/ColoredTag";

interface CommitHistoryProps {
  versions: PromptVersion[];
  onVersionClick: (version: PromptVersion) => void;
  activeVersionId: string;
  onRestoreVersionClick: (version: PromptVersion) => void;
  latestVersionId?: string;
}

const CommitHistory = ({
  versions,
  onVersionClick,
  activeVersionId,
  onRestoreVersionClick,
  latestVersionId,
}: CommitHistoryProps) => {
  const { toast } = useToast();
  const [hoveredVersionId, setHoveredVersionId] = useState<string | null>(null);

  const handleCopyClick = async (versionId: string) => {
    await copy(versionId);

    toast({
      description: "Commit successfully copied to clipboard",
    });
  };

  return (
    <>
      <ul className="max-h-[500px] overflow-y-auto overflow-x-hidden rounded border bg-background p-1">
        {versions?.map((version) => {
          return (
            <li
              key={version.id}
              className={cn(
                "cursor-pointer hover:bg-primary-foreground rounded-sm px-4 py-2.5 flex flex-col overflow-hidden",
                {
                  "bg-primary-foreground": activeVersionId === version.id,
                },
              )}
              onMouseEnter={() => setHoveredVersionId(version.id)}
              onMouseLeave={() => setHoveredVersionId(null)}
              onClick={() => onVersionClick(version)}
            >
              <div className="flex items-center gap-2 min-w-0 h-[24px]">
                <GitCommitVertical className="size-4 shrink-0 text-muted-slate" />
                <span
                  className={cn("comet-body-s shrink-0", {
                    "comet-body-s-accented": activeVersionId === version.id,
                  })}
                >
                  {version.commit}
                </span>
                <>
                  <span
                    className={cn(
                      "text-muted-slate/60 shrink-0 text-xs transition-opacity",
                      version.tags && version.tags.length > 0
                        ? "opacity-100"
                        : "opacity-0",
                    )}
                  >
                    ·
                  </span>
                  <div className="flex gap-1 overflow-hidden items-center max-w-[160px] shrink flex-nowrap">
                    {version.tags &&
                      version.tags.length > 0 &&
                      version.tags.slice(0, 3).map((tag) => (
                        <ColoredTag
                          key={tag}
                          label={tag}
                          size="xs"
                          className="max-w-[65px] truncate scale-[0.85] origin-left shrink min-w-0"
                        />
                      ))}
                    {version.tags && version.tags.length > 3 && (
                      <TooltipWrapper
                        content={
                          <div className="flex flex-col gap-1 max-w-[200px]">
                            <span className="text-xs font-medium">
                              All tags ({version.tags.length}):
                            </span>
                            <div className="flex flex-wrap gap-1">
                              {version.tags.map((tag) => (
                                <span
                                  key={tag}
                                  className="text-xs text-muted-foreground"
                                >
                                  {tag}
                                </span>
                              ))}
                            </div>
                          </div>
                        }
                      >
                        <span className="inline-flex items-center rounded px-1 py-0.5 text-[9px] font-medium bg-muted-slate/10 text-muted-slate shrink-0 whitespace-nowrap cursor-help">
                          +{version.tags.length - 3}
                        </span>
                      </TooltipWrapper>
                    )}
                  </div>
                </>
                <div
                  className={cn(
                    "ml-auto flex gap-1 shrink-0 transition-opacity",
                    hoveredVersionId === version.id
                      ? "opacity-100"
                      : "opacity-0",
                  )}
                >
                  <TooltipWrapper content="Copy commit">
                    <Button
                      size="icon-3xs"
                      variant="minimal"
                      onClick={(e) => {
                        e.stopPropagation();
                        handleCopyClick(version.commit);
                      }}
                    >
                      <Copy />
                    </Button>
                  </TooltipWrapper>
                  {version.id !== latestVersionId && (
                    <TooltipWrapper content="Restore this version">
                      <Button
                        size="icon-3xs"
                        variant="minimal"
                        onClick={(e) => {
                          e.stopPropagation();
                          onRestoreVersionClick(version);
                        }}
                      >
                        <Undo2 />
                      </Button>
                    </TooltipWrapper>
                  )}
                </div>
              </div>
              <p className="comet-body-s pl-6 text-light-slate">
                {formatDate(version.created_at)}
              </p>
            </li>
          );
        })}
      </ul>
    </>
  );
};

export default CommitHistory;
