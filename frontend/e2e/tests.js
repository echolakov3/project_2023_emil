const { Builder, By, until } = require('selenium-webdriver');
const { expect } = require('chai');

describe('E2E Tests', function () {
  let driver;

  before(async function () {
    driver = await new Builder().forBrowser('chrome').build();
  });

  after(async function () {
    setTimeout(async () => {
      await driver.quit();
    }, 10000);
  });

  it('should have more than 200 rows in the table', async function () {
    try {
      await driver.get('http://localhost:4200/');

      const table = await driver.wait(until.elementLocated(By.css('table')), 10000);

      await driver.wait(async () => {
        const rows = await table.findElements(By.css('tr'));
        return rows.length >= 200;
      }, 10000);

      const rows = await table.findElements(By.css('tr'));
      console.log('Number of rows:', rows.length);

      expect(rows.length).to.be.greaterThan(200);

      let pageSource = await driver.getPageSource();
      pageSource = pageSource.toLowerCase();
      expect(pageSource).to.contain('New York'.toLocaleLowerCase());
      expect(pageSource).to.contain('468.9');
      expect(pageSource).to.contain('8398748');
      expect(pageSource).to.contain('17911.5');
      expect(pageSource).to.contain('name');
      expect(pageSource).to.contain('area');
      expect(pageSource).to.contain('population');
      expect(pageSource).to.contain('density');
    } catch (error) {
      throw error;
    }
  });
});
