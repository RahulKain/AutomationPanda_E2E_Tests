Feature: Automation Panda Homepage
  As a visitor
  I want to access the Automation Panda blog
  So that I can read articles about software testing

  @smoke @homepage
  Scenario: Verify homepage loads successfully
    Given I am on the Automation Panda homepage
    Then the homepage should be displayed
    And the page title should contain "Automation Panda"

  @homepage
  Scenario: Verify site header is displayed
    Given I am on the Automation Panda homepage
    Then the site header should be visible
    And the header should display "Automation Panda"

  @homepage
  Scenario: Verify recent posts are displayed
    Given I am on the Automation Panda homepage
    Then I should see recent blog posts
    And there should be at least 1 post displayed
