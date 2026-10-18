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

export interface Trait {
  name: string;
  numUnits: number;
  style: number;
}

export interface Unit {
  characterId: string;
  tier: number;
  rarity: number;
  items: string[];
}

export interface Participant {
  placement: number;
  level: number;
  goldLeft: number;
  lastRound: number;
  playersEliminated: number;
  totalDamageToPlayers: number;
  you: boolean;
  augments: string[];
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
  stats: Stats;
  matches: MatchSummary[];
}

export interface MatchDetailResponse {
  matchId: string;
  gameDatetime: number;
  gameLengthSeconds: number;
  queue: string;
  setNumber: number;
  participants: Participant[];
}