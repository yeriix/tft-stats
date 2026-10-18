import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PlayerProfile } from '../../models/tft.models';
import { TftService } from '../../services/tft.services';
import {
  itemsLabel,
  placementClass,
  prettyName,
  rankLabel,
  stars,
  tierClass,
  timeAgo,
} from '../../utils/format';

@Component({
  selector: 'app-player',
  imports: [RouterLink],
  templateUrl: './player.html',
  styleUrl: './player.scss',
})
export class Player {
  private route = inject(ActivatedRoute);
  private tftService = inject(TftService);

  profile = signal<PlayerProfile | null>(null);
  loading = signal(true);
  error = signal('');

  readonly prettyName = prettyName;
  readonly itemsLabel = itemsLabel;
  readonly stars = stars;
  readonly rankLabel = rankLabel;
  readonly tierClass = tierClass;
  readonly placementClass = placementClass;
  readonly timeAgo = timeAgo;

  constructor() {
    this.route.paramMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      this.load(params.get('gameName') ?? '', params.get('tagLine') ?? '');
    });
  }

  private load(gameName: string, tagLine: string) {
    this.loading.set(true);
    this.error.set('');
    this.profile.set(null);

    this.tftService.getProfile(gameName, tagLine).subscribe({
      next: (profile) => {
        this.profile.set(profile);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.detail ?? 'Unexpected error');
        this.loading.set(false);
      },
    });
  }
}