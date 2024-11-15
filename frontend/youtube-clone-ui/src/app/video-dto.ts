export interface VideoDto{
    id: string
    userId: string
    title: string
    description: string
    aiOverview: string
    tags: Array<string>
    videoUrl: string
    videoStatus: string
    thumbnailUrl: string
    likeCount: number
    dislikeCount: number
    viewCount: number
}