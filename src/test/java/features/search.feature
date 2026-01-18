Feature: Blog Search Functionality
  As a visitor
  I want to search for articles
  So that I can find content on specific topics

  @smoke @search
  Scenario: Search for existing content
    Given I am on the Automation Panda homepage
    When I search for "selenium"
    Then search results should be displayed
    And the results should contain "selenium"

  @search
  Scenario Outline: Search for various topics
    Given I am on the Automation Panda homepage
    When I search for "<keyword>"
    Then search results should be displayed

    Examples:
      | keyword     |
      | python      |
      | testing     |
      | automation  |

  @search
  Scenario: Search with no results
    Given I am on the Automation Panda homepage
    When I search for "xyznonexistent123"
    Then no search results should be found
