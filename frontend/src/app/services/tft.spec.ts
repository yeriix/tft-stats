import { TestBed } from '@angular/core/testing';
import { TftService } from './TftService';

describe('TftService', () => {
  let service: TftService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TftService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
