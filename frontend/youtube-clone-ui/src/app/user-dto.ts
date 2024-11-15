export interface UserDto{
    id: string
    dislikedVideos : Array<String>
    emailAddress: string
    firstName: string
    fullName: string
    lastName: string
    pictureUrl: string
    likedVideos: Array<String>
    sub: string
    subscribedToUsers: Array<String>
    subscribers: Array<String>
    videoHistory: Array<String>
}