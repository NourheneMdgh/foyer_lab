import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 2,
  iterations: 4,
  thresholds: {
    http_req_duration: ['avg<2000'], // average response time under 2 seconds
    http_req_failed: ['rate==0'],    // 0% request failures
    checks: ['rate==1.0'],           // all checks (status 200) must pass
  },
};

export default function () {
  const res = http.get(
    'http://10.200.0.27:12054/global?start_time=0&end_time=99999999999&field=appName&action=count',
    {
      headers: {
        'accept': 'application/json',
      },
    }
  );

  check(res, {
    'status is 200': (r) => r.status === 200,
  });
}
