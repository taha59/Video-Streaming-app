import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UploadVideoComponent } from './upload-video/upload-video.component';
import { SaveVideoDetailsComponent } from './save-video-details/save-video-details.component';
import { VideoDetailComponent } from './video-detail/video-detail.component';
import { HomeComponent } from './home/home.component';
import { SubscriptionsComponent } from './subscriptions/subscriptions.component';
import { HistoryComponent } from './history/history.component';
import { LikedVideoComponent } from './liked-video/liked-video.component';
import { FeaturedComponent } from './featured/featured.component';
import { CallbackComponent } from './callback/callback.component';
import { VideoSearchComponent } from './video-search/video-search.component';

const routes: Routes = [
    {
      path: '',component: HomeComponent,
      children: [
        {
          path: 'subscriptions',component: SubscriptionsComponent,
        },
        {
          path: 'history',component: HistoryComponent,
        },
        {
          path: 'liked-videos',component: LikedVideoComponent,
        },
        {
          path: 'featured',component: FeaturedComponent,
        },
        {
          path: 'search',component: VideoSearchComponent,
        },

      ]
    },
    {
      path: 'upload-video',component: UploadVideoComponent,
    },
    {
      path: 'save-video-details/:videoId',component: SaveVideoDetailsComponent,
    },
    {
      path: 'video-details/:videoId',component: VideoDetailComponent,
    },
    {
      path: 'callback',component: CallbackComponent,
    },

  ];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
