import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { UnitTile } from '../../components/unit-tile/unit-tile';
import { GameMode, MatchSummary, PlayerProfile, Stats } from '../../models/tft.models';
import { TftService } from '../../services/tft.services';
import { placementClass, rankLabel, tierClass, timeAgo } from '../../utils/format';

@Component({
  selector: 'app-player',
  imports: [RouterLink, UnitTile],
  templateUrl: './player.html',
  styleUrl: './player.scss',
})
export class Player {
  private route = inject(ActivatedRoute);
  private tftService = inject(TftService);

  readonly modes: { value: GameMode; label: string }[] = [
    { value: 'all', label: 'All' },
    { value: 'ranked', label: 'Ranked' },
    { value: 'normal', label: 'Normal' },
    { value: 'hyper-roll', label: 'Hyper Roll' },
    { value: 'double-up', label: 'Double Up' },
  ];

  profile = signal<PlayerProfile | null>(null);
  stats = signal<Stats | null>(null);
  matches = signal<MatchSummary[]>([]);
  hasMore = signal(false);
  mode = signal<GameMode>('all');
  page = signal(0);

  loading = signal(true);
  loadingMatches = signal(false);
  error = signal('');

  readonly rankLabel = rankLabel;
  readonly tierClass = tierClass;
  readonly placementClass = placementClass;
  readonly timeAgo = timeAgo;

  constructor() {
    this.route.paramMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      this.loadProfile(params.get('gameName') ?? '', params.get('tagLine') ?? '');
    });
  }

  setMode(mode: GameMode) {
    if (mode === this.mode() || this.loadingMatches()) return;
    this.mode.set(mode);
    this.loadMatches();
  }

  loadMore() {
    if (this.loadingMatches()) return;
    const previous = this.page();
    this.page.set(previous + 1);
    this.loadMatches(() => this.page.set(previous));
  }

  private loadProfile(gameName: string, tagLine: string) {
    this.loading.set(true);
    this.error.set('');
    this.profile.set(null);
    this.stats.set(null);
    this.matches.set([]);
    this.hasMore.set(false);
    this.mode.set('all');
    this.page.set(0);

    this.tftService.getProfile(gameName, tagLine).subscribe({
      next: (profile) => {
        this.profile.set(profile);
        this.loading.set(false);
        this.loadMatches();
      },
      error: (err) => {
        this.error.set(err.error?.detail ?? 'Unexpected error');
        this.loading.set(false);
      },
    });
  }

  private loadMatches(onError?: () => void) {
    const profile = this.profile();
    if (!profile) return;

    this.error.set('');
    this.loadingMatches.set(true);

    this.tftService.getMatches(profile.puuid, this.mode(), this.page()).subscribe({
      next: (result) => {
        this.stats.set(result.stats);
        this.matches.set(result.matches);
        this.hasMore.set(result.hasMore);
        this.loadingMatches.set(false);
      },
      error: (err) => {
        onError?.();
        this.error.set(err.error?.detail ?? 'Unexpected error');
        this.loadingMatches.set(false);
      },
    });
  }
}