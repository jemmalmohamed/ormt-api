# Chart Configuration API

## Overview
This document describes the chart configuration functionality that allows saving and retrieving chart configurations for indicators.

## Database Schema
A new field `chart_config` has been added to the `indicateur` table to store chart configuration as a JSON string.

```sql
ALTER TABLE indicateur ADD COLUMN IF NOT EXISTS chart_config TEXT;
```

## API Endpoints

### Save Chart Configuration
**PUT** `/api/v1/admin/indicateurs/{id}/chart-config`

Updates the chart configuration for a specific indicator.

#### Request Body
```json
{
  "chartConfig": "{\"type\":\"bar\",\"data\":{\"labels\":[\"Q1\",\"Q2\",\"Q3\",\"Q4\"],\"datasets\":[{\"label\":\"Sales\",\"data\":[10,20,30,40]}]},\"options\":{\"responsive\":true}}"
}
```

#### Response
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    "id": 1,
    "nom": "Indicator Name",
    "description": "Indicator description",
    "chartConfig": "{\"type\":\"bar\",\"data\":{\"labels\":[\"Q1\",\"Q2\",\"Q3\",\"Q4\"],\"datasets\":[{\"label\":\"Sales\",\"data\":[10,20,30,40]}]},\"options\":{\"responsive\":true}}",
    // ... other indicator fields
  }
}
```

## Frontend Integration

Based on your frontend service method, you can update the API call to match the new endpoint:

```typescript
saveChartConfig(id: number, chartConfigJson: string): Observable<any> {
  const payload = {
    chartConfig: chartConfigJson  // Note: changed from nested structure to direct field
  };
  return this.put(`${this.resourceUrl}/${id}/chart-config`, payload);
}
```

## Notes
- The chart configuration is stored as a JSON string in the database
- The frontend should send the chart configuration as a JSON string in the `chartConfig` field
- The API validates that the `chartConfig` field is not blank
- The endpoint requires `indicateur:edit` permission
