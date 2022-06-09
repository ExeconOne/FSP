## Example FspRequests

---
## Available filter operations
+ EQUALS
+ NOT_EQUALS
+ CONTAINS
+ IN
+ NOT_IN
+ GREATER_THAN
+ LESS_THAN
+ GREATER_OR_EQUALS
+ LESS_OR_EQUALS

---
## Supported date formats

| Type          |    Format     |
|---------------|:-------------:|
| LocalDateTime     | '2011-12-03T10:15:30' |
| LocalDate    |   '2011-12-03'    |

---
### Operation: EQUALS

```json
{
  "filter": [
    {
      "by": "text",
      "operation": "EQUALS",
      "value": "abc",
      "operator": "AND"
    }
  ],
  "sort": [],
  "page": {
    "size": 10,
    "number": 0
  }
}
```

### Operation: NOT_EQUALS

```json
{
  "filter": [
    {
      "by": "text",
      "operation": "NOT_EQUALS",
      "value": "abc",
      "operator": "AND"
    }
  ],
  "sort": [],
  "page": {
    "size": 10,
    "number": 0
  }
}
```

### Operation: CONTAINS

```json
{
  "filter": [
    {
      "by": "text",
      "operation": "CONTAINS",
      "value": "a",
      "operator": "AND"
    }
  ],
  "sort": [],
  "page": {
    "size": 10,
    "number": 0
  }
}
```

### Operation: IN

```json
{
  "filter": [
    {
      "by": "text",
      "operation": "IN",
      "value": [
        "abc",
        "dfg"
      ],
      "operator": "AND"
    }
  ],
  "sort": [],
  "page": {
    "size": 10,
    "number": 0
  }
}
```

### Operation NOT_IN

```json
{
  "filter": [
    {
      "by": "text",
      "operation": "NOT_IN",
      "value": [
        "abc",
        "dfg"
      ],
      "operator": "AND"
    }
  ],
  "sort": [],
  "page": {
    "size": 10,
    "number": 0
  }
}
```

### Operation GREATER_THAN

```json
{
  "filter": [
    {
      "by": "number",
      "operation": "GREATER_THAN",
      "value": 1.2,
      "operator": "AND"
    }
  ],
  "sort": [],
  "page": {
    "size": 10,
    "number": 0
  }
}
```

### Operation GREATER_OR_EQUALS

```json
{
  "filter": [
    {
      "by": "number",
      "operation": "GREATER_OR_EQUALS",
      "value": 1.2,
      "operator": "AND"
    }
  ],
  "sort": [],
  "page": {
    "size": 10,
    "number": 0
  }
}
```

### Operation LESS_THAN

```json
{
  "filter": [
    {
      "by": "number",
      "operation": "LESS_THAN",
      "value": 11.2,
      "operator": "AND"
    }
  ],
  "sort": [],
  "page": {
    "size": 10,
    "number": 0
  }
}
```

### Operation LESS_OR_EQUALS

```json
{
  "filter": [
    {
      "by": "number",
      "operation": "LESS_OR_EQUALS",
      "value": 11.2,
      "operator": "AND"
    }
  ],
  "sort": [],
  "page": {
    "size": 10,
    "number": 0
  }
}
```

## Example of Filter and Sort

```json
{
  "filter": [
    {
      "by": "number",
      "operation": "LESS_OR_EQUALS",
      "value": 11.2,
      "operator": "AND"
    }
  ],
  "sort": [
    {
      "by": "number",
      "direction": "DESC"
    }
  ],
  "page": {
    "size": 10,
    "number": 0
  }
}
```

## Example of combining filters
```json
{
  "filter": [
    {
      "by": "text",
      "operation": "EQUALS",
      "value": "abc",
      "operator": "AND"
    },
    {
      "by": "number",
      "operation": "EQUALS",
      "value": "1.2",
      "operator": "OR"
    },
    {
      "by": "number",
      "operation": "EQUALS",
      "value": "11.2",
      "operator": "OR"
    }
  ],
  "sort": [
    {
      "by": "number",
      "direction": "DESC"
    }
  ],
  "page": {
    "size": 10,
    "number": 0
  }
}
```

---
### [Back to README](README.md)
