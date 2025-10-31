Explanation of heuristic and changes made to the utility function
- How this is an improvement to -1, 1 and 0 since when the agent starts to lose or win by a lot, every path explored
  gives the same value since Depth moves isn't enough for them to possibly catch up
- However, by returning a wider range of values, the agent can pick the action which catches it up to the other player or puts it ahead of the other player as much as possible