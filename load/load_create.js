import http from "k6/http";
import { Counter } from "k6/metrics";
import exec from "k6/execution";

export const options = {
    scenarios: {
        create_keys: {
            executor: "constant-arrival-rate",
            rate: 2000,
            timeUnit: "1s",
            duration: "5s",
            preAllocatedVUs: 50,
            maxVUs: 200,
        }
    }
};

const successCounter = new Counter("success_count");
const errorCounter = new Counter("error_count");
const totalCounter = new Counter("total_requests");

export default function () {
    const url = "http://public-elb-120166699.us-east-1.elb.amazonaws.com/api/keys/check-or-create";
    const randomKey = Math.floor(Math.random() * 1_000_000_000).toString();

    const payload = JSON.stringify({
        keyValue: "loadtest_6_02_" + randomKey,
        accountNumber: "123456789",
        ownerDocument: "123456789",
        entityCode: "BA"
    });

    const headers = { "Content-Type": "application/json" };
    const res = http.post(url, payload, { headers });

    totalCounter.add(1);

    // Define qué consideras como éxito/error
    if (res.status === 200 || res.status === 404) {
        successCounter.add(1);
    } else {
        errorCounter.add(1);
        console.error(`Error en request: status ${res.status}`);
    }

    if (exec.scenario.iterationInTest % 100 === 0) {
        console.log(`Progress: ${exec.scenario.iterationInTest} requests completed`);
    }
}

export function handleSummary(data) {
    // Usar los contadores customizados que registraste
    const totalRequests = data.metrics.total_requests?.values?.count || 0;
    const successRequests = data.metrics.success_count?.values?.count || 0;
    const failedRequests = data.metrics.error_count?.values?.count || 0;

    const httpReqDuration = data.metrics.http_req_duration?.values || {};
    const min = httpReqDuration.min || 0;
    const max = httpReqDuration.max || 0;
    const avg = httpReqDuration.avg || 0;
    const med = httpReqDuration.med || 0;
    const p90 = httpReqDuration["p(90)"] || 0;
    const p95 = httpReqDuration["p(95)"] || 0;
    const p99 = httpReqDuration["p(99)"] || 0;

    const meetsRequirement = p95 > 0 && p95 < 200;
    const successRate = totalRequests > 0 ? ((successRequests / totalRequests) * 100) : 0;

    const summary = `
╔════════════════════════════════════════════════════════════╗
║             LOAD TEST RESULTS - Aurora + Redis             ║
╚════════════════════════════════════════════════════════════╝

Request Summary:
   Total Requests:      ${totalRequests}
   Successful:          ${successRequests}
   Failed:              ${failedRequests}
   Success Rate:        ${successRate.toFixed(2)}%

Latency Stats (ms):
   Min:                 ${min.toFixed(2)}
   Average:            ${avg.toFixed(2)}
   Median:              ${med.toFixed(2)}
   P90:                 ${p90.toFixed(2)}
   P95:                 ${p95.toFixed(2)}
   P99:                 ${p99.toFixed(2)}
   Max:                 ${max.toFixed(2)}

Requirements Check:
   Target RPS:          200 req/sec
   Target P95 Latency:  < 200 ms
   Actual P95 Latency:  ${p95.toFixed(2)} ms
   Status:              ${meetsRequirement ? '✓ PASSED' : '✗ FAILED'}
`;

    return {
        stdout: summary
    };
}