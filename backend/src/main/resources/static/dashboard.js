document.addEventListener("DOMContentLoaded", () => {
  const BASE_URL = "/api/actuator";

  // Safe fetch function
  async function fetchActuatorData(endpoint) {
    try {
      const res = await fetch(`${BASE_URL}/${endpoint}`);
      console.log("Fetching from : ${res}")
      if (!res.ok) throw new Error(res.statusText);
      const text = await res.text();
      if (!text) return {};
      return JSON.parse(text);
    } catch (err) {
      console.warn("Failed to fetch:", endpoint, err);
      return {};
    }
  }

  // Format values with units
  function formatValue(value, unit) {
    if (value == null) return "N/A";
    if (unit === "bytes") {
      if (value > 1e9) return (value / 1e9).toFixed(2) + " GB";
      if (value > 1e6) return (value / 1e6).toFixed(2) + " MB";
      if (value > 1e3) return (value / 1e3).toFixed(2) + " KB";
      return value + " B";
    }
    if (unit === "ms") return value.toFixed(2) + " ms";
    if (unit === "seconds") return value.toFixed(2) + " s";
    if (unit === "percent") return (value * 100).toFixed(1) + "%";
    if (unit === "count") return value.toFixed(0);
    return value.toFixed(2);
  }

  // Metric metadata placeholder
  const metricMeta = {
    // keep all your metricMeta as is
    "application.ready.time": { desc: "Time to be ready", unit: "ms" },
    "application.started.time": { desc: "Time to start app", unit: "ms" },
    "disk.free": { desc: "Free disk space", unit: "bytes" },
    "disk.total": { desc: "Total disk space", unit: "bytes" },
    "executor.active": { desc: "Active executor threads", unit: "count" },
    "executor.completed": { desc: "Completed executor tasks", unit: "count" },
    "executor.pool.core": { desc: "Core pool size", unit: "count" },
    "executor.pool.max": { desc: "Max pool size", unit: "count" },
    "executor.pool.size": { desc: "Current pool size", unit: "count" },
    "executor.queue.remaining": {
      desc: "Remaining queue capacity",
      unit: "count",
    },
    "executor.queued": { desc: "Queued tasks", unit: "count" },
    "hikaricp.connections": { desc: "Connections (HikariCP)", unit: "count" },
    "hikaricp.connections.acquire": {
      desc: "Connection acquire time",
      unit: "ms",
    },
    "hikaricp.connections.active": {
      desc: "Active connections",
      unit: "count",
    },
    "hikaricp.connections.creation": {
      desc: "Connection creation time",
      unit: "ms",
    },
    "hikaricp.connections.idle": { desc: "Idle connections", unit: "count" },
    "hikaricp.connections.max": {
      desc: "Max connections allowed",
      unit: "count",
    },
    "hikaricp.connections.min": {
      desc: "Min connections allowed",
      unit: "count",
    },
    "hikaricp.connections.pending": {
      desc: "Pending connections",
      unit: "count",
    },
    "hikaricp.connections.timeout": { desc: "Connection timeout", unit: "ms" },
    "hikaricp.connections.usage": { desc: "Connection usage time", unit: "ms" },
    "http.server.requests": { desc: "HTTP requests", unit: "count" },
    "http.server.requests.active": {
      desc: "Active HTTP requests",
      unit: "count",
    },
    "jdbc.connections.active": {
      desc: "Active JDBC connections",
      unit: "count",
    },
    "jdbc.connections.idle": { desc: "Idle JDBC connections", unit: "count" },
    "jdbc.connections.max": { desc: "Max JDBC connections", unit: "count" },
    "jdbc.connections.min": { desc: "Min JDBC connections", unit: "count" },
    "jvm.buffer.count": { desc: "Number of buffers", unit: "count" },
    "jvm.buffer.memory.used": { desc: "Buffer memory used", unit: "bytes" },
    "jvm.buffer.total.capacity": {
      desc: "Buffer total capacity",
      unit: "bytes",
    },
    "jvm.classes.loaded": { desc: "Loaded classes", unit: "count" },
    "jvm.classes.unloaded": { desc: "Unloaded classes", unit: "count" },
    "jvm.compilation.time": { desc: "JIT compilation time", unit: "ms" },
    "jvm.gc.concurrent.phase.time": {
      desc: "GC concurrent phase time",
      unit: "ms",
    },
    "jvm.gc.live.data.size": { desc: "Live data size after GC", unit: "bytes" },
    "jvm.gc.max.data.size": { desc: "Max GC data size", unit: "bytes" },
    "jvm.gc.memory.allocated": { desc: "Memory allocated", unit: "bytes" },
    "jvm.gc.memory.promoted": { desc: "Memory promoted", unit: "bytes" },
    "jvm.gc.overhead": { desc: "GC overhead time", unit: "percent" },
    "jvm.gc.pause": { desc: "GC pause time", unit: "ms" },
    "jvm.memory.committed": { desc: "Committed memory", unit: "bytes" },
    "jvm.memory.max": { desc: "Max memory", unit: "bytes" },
    "jvm.memory.usage.after.gc": {
      desc: "Memory usage after GC",
      unit: "bytes",
    },
    "jvm.memory.used": { desc: "Used memory", unit: "bytes" },
    "jvm.threads.daemon": { desc: "Daemon threads", unit: "count" },
    "jvm.threads.live": { desc: "Live threads", unit: "count" },
    "jvm.threads.peak": { desc: "Peak threads", unit: "count" },
    "jvm.threads.started": { desc: "Started threads", unit: "count" },
    "jvm.threads.states": { desc: "Thread states", unit: "count" },
    "logback.events": { desc: "Logback events", unit: "count" },
    "process.cpu.time": { desc: "Process CPU time", unit: "seconds" },
    "process.cpu.usage": { desc: "Process CPU usage", unit: "percent" },
    "process.start.time": { desc: "Process start time", unit: "seconds" },
    "process.uptime": { desc: "Process uptime", unit: "seconds" },
    "spring.data.repository.invocations": {
      desc: "Repository method calls",
      unit: "count",
    },
    "spring.security.authorizations": { desc: "Authorizations", unit: "count" },
    "spring.security.authorizations.active": {
      desc: "Active authorizations",
      unit: "count",
    },
    "spring.security.filterchains": {
      desc: "Security filter chains",
      unit: "count",
    },
    "system.cpu.count": { desc: "Available CPU cores", unit: "count" },
    "system.cpu.usage": { desc: "System CPU usage", unit: "percent" },
    "tomcat.sessions.active.current": {
      desc: "Active sessions",
      unit: "count",
    },
    "tomcat.sessions.active.max": {
      desc: "Max active sessions",
      unit: "count",
    },
    "tomcat.sessions.alive.max": { desc: "Max alive sessions", unit: "count" },
    "tomcat.sessions.created": { desc: "Sessions created", unit: "count" },
    "tomcat.sessions.expired": { desc: "Sessions expired", unit: "count" },
    "tomcat.sessions.rejected": { desc: "Sessions rejected", unit: "count" },
  };

  // Status badge helper
  function getStatusBadge(level) {
    if (level === "ok")
      return `<span class="px-2 py-1 rounded text-xs bg-green-200 text-green-800 font-semibold">✅ OK</span>`;
    if (level === "warn")
      return `<span class="px-2 py-1 rounded text-xs bg-yellow-200 text-yellow-800 font-semibold">⚠️ Warning</span>`;
    if (level === "crit")
      return `<span class="px-2 py-1 rounded text-xs bg-red-200 text-red-800 font-semibold">🔴 Critical</span>`;
    return `<span class="px-2 py-1 rounded text-xs bg-gray-200 text-gray-800">N/A</span>`;
  }

  // Health
  async function loadHealth() {
    const data = await fetchActuatorData("health");
    const statusEl = document.getElementById("health-status");
    statusEl.textContent = data.status || "N/A";
    statusEl.className =
      data.status === "UP"
        ? "text-green-600 font-bold"
        : "text-red-600 font-bold";
  }
  loadHealth();

  // Metrics Table
  async function loadMetricsTable() {
    const data = await fetchActuatorData("metrics");
    const table = document.getElementById("metrics-table");
    if (!data.names) return;

    table.innerHTML = data.names
      .map((name) => {
        const meta = metricMeta[name] || { desc: "----", unit: "" };
        return `
        <tr class="metric-row" data-name="${name}">
          <td class="border border-gray-300 px-2 py-1">${name}</td>
          <td class="border border-gray-300 px-2 py-1" id="metric-${name}">Loading...</td>
          <td class="border border-gray-300 px-2 py-1">${meta.desc} ${
          meta.unit ? `(${meta.unit})` : ""
        }</td>
          <td class="border border-gray-300 px-2 py-1" id="status-${name}">N/A</td>
        </tr>
      `;
      })
      .join("");

    // Fetch all metrics in parallel
    await Promise.all(
      data.names.map(async (name) => {
        const metric = await fetchActuatorData(`metrics/${name}`);
        const cell = document.getElementById(`metric-${name}`);
        const statusCell = document.getElementById(`status-${name}`);
        const meta = metricMeta[name] || { unit: "" };

        if (metric.measurements?.length > 0) {
          const raw = metric.measurements[0].value;
          cell.textContent = formatValue(raw, meta.unit);
          statusCell.innerHTML = getStatusBadge("ok");
        } else {
          cell.textContent = "N/A";
          statusCell.innerHTML = getStatusBadge();
        }
      })
    );
  }

  // Metrics Search
  document.getElementById("metrics-search").addEventListener("input", (e) => {
    const query = e.target.value.toLowerCase();
    document.querySelectorAll(".metric-row").forEach((row) => {
      const name = row.dataset.name.toLowerCase();
      row.style.display = name.includes(query) ? "" : "none";
    });
  });

  // Charts initialization
  const cpuChart = new Chart(document.getElementById("cpuChart"), {
    type: "line",
    data: {
      labels: [],
      datasets: [
        {
          label: "CPU %",
          data: [],
          borderColor: "rgb(75,192,192)",
          fill: false,
        },
      ],
    },
  });

  const memoryChart = new Chart(document.getElementById("memoryChart"), {
    type: "line",
    data: {
      labels: [],
      datasets: [
        {
          label: "Heap/Non-Heap MB",
          data: [],
          borderColor: "rgb(255,99,132)",
          fill: false,
        },
      ],
    },
  });

  const rpsChart = new Chart(document.getElementById("rpsChart"), {
    type: "line",
    data: {
      labels: [],
      datasets: [
        {
          label: "Requests/sec",
          data: [],
          borderColor: "rgb(54,162,235)",
          fill: false,
        },
      ],
    },
  });

  const latencyChart = new Chart(document.getElementById("latencyChart"), {
    type: "line",
    data: {
      labels: [],
      datasets: [
        {
          label: "Latency (ms)",
          data: [],
          borderColor: "rgb(255,206,86)",
          fill: false,
        },
      ],
    },
  });

  const endpointLatencyChart = new Chart(
    document.getElementById("endpointLatencyChart"),
    {
      type: "bar",
      data: {
        labels: [],
        datasets: [
          {
            label: "Avg Latency (ms)",
            data: [],
            backgroundColor: "rgba(54,162,235,0.6)",
          },
        ],
      },
      options: { responsive: true, scales: { y: { beginAtZero: true } } },
    }
  );

  // HTTP metrics charts
  const httpStatusChart = new Chart(
    document.getElementById("httpStatusChart"),
    {
      type: "bar",
      data: {
        labels: ["2xx", "3xx", "4xx", "5xx"],
        datasets: [
          {
            label: "Count",
            data: [0, 0, 0, 0],
            backgroundColor: ["green", "blue", "orange", "red"],
          },
        ],
      },
      options: { responsive: true, scales: { y: { beginAtZero: true } } },
    }
  );

  const topEndpointsChart = new Chart(
    document.getElementById("topEndpointsChart"),
    {
      type: "bar",
      data: {
        labels: [],
        datasets: [
          {
            label: "Avg Latency (ms)",
            data: [],
            backgroundColor: "rgba(54,162,235,0.6)",
          },
        ],
      },
      options: { responsive: true, scales: { y: { beginAtZero: true } } },
    }
  );

  const errorRateChart = new Chart(document.getElementById("errorRateChart"), {
    type: "line",
    data: {
      labels: [],
      datasets: [
        { label: "Error %", data: [], borderColor: "red", fill: false },
      ],
    },
  });

  let lastRequestCount = 0;

  // Endpoint Latency and HTTP metrics loader
  let loadingEndpoint = false;
  async function loadEndpointLatency() {
    if (loadingEndpoint) return;
    loadingEndpoint = true;

    const data = await fetchActuatorData("metrics/http.server.requests");
    const table = document.getElementById("endpoint-latency-table");
    table.innerHTML = "";

    const uriTag = data.availableTags?.find((t) => t.tag === "uri");
    const methodTag = data.availableTags?.find((t) => t.tag === "method");
    const endpoints = uriTag?.values || [];
    const methods = methodTag?.values || [];

    const chartLabels = [];
    const chartData = [];
    const statusCounts = { 2: 0, 3: 0, 4: 0, 5: 0 };

    for (const endpoint of endpoints) {
      if (!endpoint || endpoint.startsWith("/actuator") || endpoint === "root")
        continue;

      for (const method of methods) {
        try {
          const encodedEndpoint = encodeURIComponent(endpoint);
          const metric = await fetchActuatorData(
            `metrics/http.server.requests?tag=uri:${encodedEndpoint}&tag=method:${method}`
          );

          if (!metric.measurements || metric.measurements.length === 0)
            continue;

          const count =
            metric.measurements.find((m) => m.statistic === "COUNT")?.value ||
            0;
          const totalTime =
            metric.measurements.find((m) => m.statistic === "TOTAL_TIME")
              ?.value || 0;
          const maxTime =
            metric.measurements.find((m) => m.statistic === "MAX")?.value || 0;
          const avgLatency = count > 0 ? (totalTime / count) * 1000 : 0;

          if (count === 0) continue;

          table.innerHTML += `
          <tr>
            <td class="border border-gray-300 px-2 py-1">${endpoint}</td>
            <td class="border border-gray-300 px-2 py-1">${method}</td>
            <td class="border border-gray-300 px-2 py-1">${count}</td>
            <td class="border border-gray-300 px-2 py-1">${avgLatency.toFixed(
              2
            )}</td>
            <td class="border border-gray-300 px-2 py-1">${(
              maxTime * 1000
            ).toFixed(2)}</td>
            <td class="border border-gray-300 px-2 py-1"></td>
          </tr>
        `;

          chartLabels.push(`${endpoint} [${method}]`);
          chartData.push(avgLatency);

          // Example: all 2xx for now
          statusCounts[2] += count;
        } catch (err) {
          console.warn("Skipping missing metric:", endpoint, method, err);
          continue;
        }
      }
    }

    // Safe chart updates
    if (typeof endpointLatencyChart !== "undefined" && endpointLatencyChart) {
      endpointLatencyChart.data.labels = chartLabels;
      endpointLatencyChart.data.datasets[0].data = chartData;
      endpointLatencyChart.update();
    }

    if (httpStatusChart && httpStatusChart.data.datasets[0]) {
      httpStatusChart.data.datasets[0].data = [
        statusCounts[2],
        statusCounts[3],
        statusCounts[4],
        statusCounts[5],
      ];
      httpStatusChart.update();
    }

    if (topEndpointsChart && topEndpointsChart.data.datasets[0]) {
      const topN = 5;
      const topIndexes = chartData
        .map((v, i) => [v, i])
        .sort((a, b) => b[0] - a[0])
        .slice(0, topN)
        .map((a) => a[1]);

      topEndpointsChart.data.labels = topIndexes.map((i) => chartLabels[i]);
      topEndpointsChart.data.datasets[0].data = topIndexes.map(
        (i) => chartData[i]
      );
      topEndpointsChart.update();
    }

    if (errorRateChart && errorRateChart.data.datasets[0]) {
      const total = Object.values(statusCounts).reduce((a, b) => a + b, 0);
      const errorPercent =
        total > 0 ? ((statusCounts[4] + statusCounts[5]) / total) * 100 : 0;
      errorRateChart.data.labels.push(new Date().toLocaleTimeString());
      errorRateChart.data.datasets[0].data.push(errorPercent.toFixed(2));
      if (errorRateChart.data.labels.length > 10) {
        errorRateChart.data.labels.shift();
        errorRateChart.data.datasets[0].data.shift();
      }
      errorRateChart.update();
    }

    loadingEndpoint = false;
  }

  // Periodic updates
  let lastMetricsTime = Date.now();
  setInterval(async () => {
    const now = new Date().toLocaleTimeString();

    // CPU
    const cpu = await fetchActuatorData("metrics/system.cpu.usage");
    const cpuVal = cpu.measurements?.[0]?.value || 0;
    cpuChart.data.labels.push(now);
    cpuChart.data.datasets[0].data.push((cpuVal * 100).toFixed(2));
    if (cpuChart.data.labels.length > 10) {
      cpuChart.data.labels.shift();
      cpuChart.data.datasets[0].data.shift();
    }
    cpuChart.update();

    // Memory
    const mem = await fetchActuatorData("metrics/jvm.memory.used");
    const memVal = mem.measurements?.[0]?.value / (1024 * 1024);
    memoryChart.data.labels.push(now);
    memoryChart.data.datasets[0].data.push(memVal.toFixed(2));
    if (memoryChart.data.labels.length > 10) {
      memoryChart.data.labels.shift();
      memoryChart.data.datasets[0].data.shift();
    }
    memoryChart.update();

    // Requests
    const req = await fetchActuatorData("metrics/http.server.requests");
    const totalCount =
      req.measurements?.find((m) => m.statistic === "COUNT")?.value || 0;
    const totalTime =
      req.measurements?.find((m) => m.statistic === "TOTAL_TIME")?.value || 0;
    const avgLatency = totalCount > 0 ? (totalTime / totalCount) * 1000 : 0;
    const rps = lastRequestCount > 0 ? (totalCount - lastRequestCount) / 5 : 0;
    lastRequestCount = totalCount;

    rpsChart.data.labels.push(now);
    rpsChart.data.datasets[0].data.push(rps.toFixed(2));
    if (rpsChart.data.labels.length > 10) {
      rpsChart.data.labels.shift();
      rpsChart.data.datasets[0].data.shift();
    }
    rpsChart.update();

    latencyChart.data.labels.push(now);
    latencyChart.data.datasets[0].data.push(avgLatency.toFixed(2));
    if (latencyChart.data.labels.length > 10) {
      latencyChart.data.labels.shift();
      latencyChart.data.datasets[0].data.shift();
    }
    latencyChart.update();

    await loadEndpointLatency();
  }, 5000);

  // Load initial metrics
  loadMetricsTable();
  setInterval(loadMetricsTable, 10000);
});
