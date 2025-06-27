import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';

const addReservationTrend = new Trend('T_add_reservation_res_time');

export const options = {
    scenarios: {
        add_20_reservations: {
            executor: 'per-vu-iterations',
            vus: 20,
            iterations: 1,
            maxDuration: '1m',
        },
    },
    // Define thresholds for the test to pass or fail.
    thresholds: {
        'http_req_failed': ['rate<0.01'], // Fail the test if more than 1% of requests fail.
        'http_req_duration': ['p(95)<500'], // Fail if 95% of requests take longer than 500ms.
        'T_add_reservation_res_time': ['p(95)<500'], // Custom metric threshold.
    },
};

// --- Test Logic ---
export default function () {
    const BASE_URL = 'http://localhost:8089/tpfoyer';
    const endpoint = '/reservation/add-reservation';

    const studentId = __VU;
    const chambreId = 100 + __VU;

    const payload = JSON.stringify({
        annee_universitaire: '2025-09-01T00:00:00Z',
        est_valide: true,
        etudiants: [{ id_etudiant: studentId }],
        chambres: [{ id_chambre: chambreId }]
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(`${BASE_URL}${endpoint}`, payload, params);

    addReservationTrend.add(res.timings.duration);

    check(res, {
        '1. status is 200 OK or 201 Created': (r) => r.status === 200 || r.status === 201,
        '2. response body is not empty': (r) => r.body.length > 0,
        '3. response contains a valid reservation ID': (r) => {
            try {
                const reservation = r.json();
                return reservation && reservation.id_reservation;
            } catch (e) {
                return false;
            }
        },
    });

    sleep(1);
}