import { TestBed, waitForAsync } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DataService } from './services/data.service';
import { of } from 'rxjs';
import { HttpClient } from '@angular/common/http';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [DataService],
      declarations: [
        AppComponent
      ],
      imports: [HttpClientTestingModule],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have as title 'frontend'`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('frontend');
  });

  it('should render title', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('hello world from frontend');
  });

  it('ngOnInit load data', waitForAsync(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;

    const httpService = TestBed.inject(HttpClient);
    const dataService = TestBed.inject(DataService);

    const cities: any = [{ "name": "New York", "area": 468.9, "population": 8398748, "density": 17911.5 }, { "name": "Los Angeles", "area": 468.7, "population": 3990456, "density": 8513.8 }];
    const citiesPageTwo: any = [{ "name": "Chicago", "area": 227.3, "population": 2705994, "density": 11904.9 }, { "name": "Houston", "area": 599.6, "population": 2320268, "density": 3869.6 }];
    spyOn(httpService, 'get').and.returnValues(
      of({ content: cities, last: false }),
      of({ content: citiesPageTwo, last: true })
    );

    const dataServiceSpy = spyOn(dataService, "loadData").and.callThrough()
    spyOn(dataService, "getLastPageReached").and.callThrough()
    spyOn(dataService['lastPageReached'], 'next').and.callThrough();

    fixture.whenStable().then(() => {
      fixture.detectChanges();

      expect(httpService.get).toHaveBeenCalled();
      expect(dataService.loadData).toHaveBeenCalled();
      expect(dataServiceSpy.calls.count()).toBe(2);
      expect(dataService.getLastPageReached).toHaveBeenCalled();
      expect(dataService['lastPageReached'].next).toHaveBeenCalled();
      expect(app.cities).toBeTruthy();
      expect(app.cities as any).toEqual(cities.concat(citiesPageTwo) as any);

      let tableText = fixture.nativeElement.querySelector('table').textContent;
      tableText = tableText.toString().toLowerCase();
      expect(tableText).toContain(cities[0].name.toString().toLowerCase());
      expect(tableText).toContain(cities[0].area.toString().toLowerCase());
      expect(tableText).toContain(cities[0].population.toString().toLowerCase());
      expect(tableText).toContain(citiesPageTwo[1].name.toString().toLowerCase());
      expect(tableText).toContain(citiesPageTwo[1].area.toString().toLowerCase());
      expect(tableText).toContain(citiesPageTwo[1].population.toString().toLowerCase());
      expect(tableText).toContain("density");
      expect(app.cities[0]["density"]).toBeDefined();
    });
  }));

});