import { TestBed, waitForAsync } from '@angular/core/testing';

import { DataService } from './data.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { HttpClient } from '@angular/common/http';

describe('DataService', () => {
  let service: DataService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(DataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('load data', () => {
    const httpService = TestBed.inject(HttpClient);

    const cities: any = [{ "name": "New York", "area": 468.9, "population": 8398748, "density": 17911.5 }, { "name": "Los Angeles", "area": 468.7, "population": 3990456, "density": 8513.8 }];
    const citiesPageTwo: any = [{ "name": "Chicago", "area": 227.3, "population": 2705994, "density": 11904.9 }, { "name": "Houston", "area": 599.6, "population": 2320268, "density": 3869.6 }];
    spyOn(httpService, 'get').and.returnValues(
      of({ content: cities, last: false }),
      of({ content: citiesPageTwo, last: true })
    );

    const dataServiceSpy = spyOn(service, "loadData").and.callThrough()
    spyOn(service, "getLastPageReached").and.callThrough()
    spyOn(service['lastPageReached'], 'next').and.callThrough();

    service.loadData();

    expect(httpService.get).toHaveBeenCalled();
    expect(service.loadData).toHaveBeenCalled();
    expect(dataServiceSpy.calls.count()).toBe(2);
    expect(service['lastPageReached'].next).toHaveBeenCalledWith(cities.concat(citiesPageTwo));
    expect(service["cities"]).toBeTruthy();
    expect(service["cities"] as any).toEqual(cities.concat(citiesPageTwo) as any)
  });
});
