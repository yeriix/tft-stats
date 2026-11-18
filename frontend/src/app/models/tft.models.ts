export interface Rank {
  tier: string;
  division: string;
  leaguePoints: number;
}

export interface Stats {
  gamesAnalyzed: number;
  averagePlacement: number;
  top4Rate: number;
  winRate: number;
}

export interface SpriteRef {
  sprite: string;
  x: number;
  y: number;
  w: number;
  h: number;
}

export interface Item {
  id: string;
  name: string;
  icon: SpriteRef | null;
}

export interface Trait {
  name: string;
  numUnits: number;
  style: number;
  icon: string | null;
}

export interface Unit {
  characterId: string;
  name: string;
  cost: number;
  tier: number;
  icon: SpriteRef | null;
  items: Item[];
}

export interface Participant {
  placement: number;
  level: number;
  goldLeft: number;
  lastRound: number;
  you: boolean;
  gameName: string | null;
  tagLine: string | null;
  traits: Trait[];
  units: Unit[];
}

export interface MatchSummary {
  matchId: string;
  gameDatetime: number;
  queue: string;
  player: Participant;
}

export interface PlayerProfile {
  puuid: string;
  gameName: string;
  tagLine: string;
  ranked: Rank | null;
  profileIconUrl: string | null;
  summonerLevel: number;
}

export interface MatchPage {
  stats: Stats;
  matches: MatchSummary[];
  hasMore: boolean;
}

export interface MatchDetailResponse {
  matchId: string;
  gameDatetime: number;
  gameLengthSeconds: number;
  queue: string;
  setNumber: number;
  participants: Participant[];
}

export type GameMode = 'all' | 'ranked' | 'normal' | 'hyper-roll' | 'double-up';