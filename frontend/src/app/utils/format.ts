import { Rank } from '../models/tft.models';

const APEX_TIERS = ['MASTER', 'GRANDMASTER', 'CHALLENGER'];

export function prettyName(raw: string): string {
  return raw
    .replace(/^TFT\d*_(Item_)?/i, '')
    .replace(/^Augment_/i, '')
    .replace(/([a-z])([A-Z])/g, '$1 $2')
    .replace(/_/g, ' ')
    .trim();
}

export function itemsLabel(items: string[]): string {
  return items.map(prettyName).join(', ');
}

export function stars(tier: number): string {
  return '★'.repeat(Math.max(tier, 1));
}

export function rankLabel(rank: Rank): string {
  const tier = rank.tier.charAt(0) + rank.tier.slice(1).toLowerCase();
  return APEX_TIERS.includes(rank.tier) ? tier : `${tier} ${rank.division}`;
}

export function tierClass(tier: string): string {
  return 'tier-' + tier.toLowerCase();
}

export function placementClass(placement: number): string {
  if (placement === 1) return 'place-1';
  if (placement <= 4) return 'place-top4';
  return 'place-bottom4';
}

export function timeAgo(epochMs: number): string {
  const minutes = Math.floor(Math.max(0, Date.now() - epochMs) / 60000);
  if (minutes < 1) return 'just now';
  if (minutes < 60) return `${minutes} min ago`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours} h ago`;
  return `${Math.floor(hours / 24)} d ago`;
}

export function formatDate(epochMs: number): string {
  return new Date(epochMs).toLocaleString();
}

export function formatDuration(seconds: number): string {
  const minutes = Math.floor(seconds / 60);
  const rest = seconds % 60;
  return `${minutes}:${rest.toString().padStart(2, '0')}`;
}