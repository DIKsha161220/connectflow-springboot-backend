const $ = (id) => document.getElementById(id);

// ConnectFlow Backend API (Spring Boot in d:\CRM-BACKEND12)
const isLocal = window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1";
const API_URL = isLocal ? "http://localhost:8080/api" : "/api";

// Roles definition
const roles = {
  admin: {
    label: "Administrative",
    user: "Amit Patil",
    initials: "AP",
    email: "amit.patil@example.com",
    permissions: ["dashboard", "customers", "calls", "visits", "followups"],
    canAddInstitute: true,
    canCall: true,
    canVisit: true,
    scope: "all"
  },
  calling: {
    label: "Calling Team",
    user: "Neha Shah",
    initials: "NS",
    email: "neha.shah@example.com",
    permissions: ["dashboard", "customers", "calls", "followups"],
    canAddInstitute: false,
    canCall: true,
    canVisit: false,
    scope: "assigned"
  },
  field: {
    label: "On-field Employee",
    user: "Rohit More",
    initials: "RM",
    email: "rohit.more@example.com",
    permissions: ["dashboard", "customers", "calls", "visits", "followups"],
    canAddInstitute: false,
    canCall: true,
    canVisit: true,
    scope: "assigned"
  }
};

let currentRole = "admin";
function activeRole() { return roles[currentRole] || roles.admin; }
function visibleCustomers() { return activeRole().scope === "all" ? state.customers : state.customers.filter(c => c.owner === activeRole().user); }
function visibleNames() { return new Set(visibleCustomers().map(c => c.name)); }
function canAccessPage(page) { return activeRole().permissions.includes(page); }

function applyRole() {
  const r = activeRole();
  const userNameEl = $("current-user-name");
  const userRoleEl = $("current-user-role");
  const topAvatarEl = $("current-user-avatar");
  const sideAvatarEl = $("sidebar-user-avatar") || document.querySelector(".sidebar-bottom .avatar");
  
  if (userNameEl) userNameEl.textContent = r.user;
  if (userRoleEl) userRoleEl.textContent = r.label;
  if (topAvatarEl) topAvatarEl.textContent = r.initials;
  if (sideAvatarEl) sideAvatarEl.textContent = r.initials;

  const roleBanner = $("role-banner");
  if (roleBanner) {
    roleBanner.textContent = r.scope === "all"
      ? "Administrative role: full access to all institutes and activities."
      : `${r.label}: showing only institutes assigned to ${r.user}.`;
  }

  document.querySelectorAll(".nav-link").forEach(b => {
    const page = b.dataset.page;
    const allowed = canAccessPage(page);
    b.classList.toggle("hidden", !allowed);
  });

  const primaryBtn = $("primary-action");
  if (primaryBtn) {
    const allowed = (state.page === "customers" && r.canAddInstitute) ||
                    (state.page === "calls" && r.canCall) ||
                    (state.page === "visits" && r.canVisit) ||
                    (state.page === "followups" && currentRole === "admin") ||
                    (state.page === "dashboard" && r.canAddInstitute);
    primaryBtn.classList.toggle("hidden", !allowed);
  }

  if (!canAccessPage(state.page)) state.page = "dashboard";
  setPage(state.page);
}

// Generate sample institutes to display 60 active records matching the dashboard screenshot
function generateInitialInstitutes() {
  const base = [
    {id:1, name:"Modern College Pune", phone:"9822011223", company:"Modern College of Arts, Science & Commerce", street:"Shivajinagar", village:"Shivajinagar", taluka:"Haveli", district:"Pune", state:"Maharashtra", pin:"411005", contacts:"Dr. Sanjay Kharat | Principal | 9822011223", referenceBy:"Amit Patil", referenceFrom:"Website", branchCount:"3", priority:"High", owner:"Amit Patil", status:"Interested", followupDate:"2026-11-25T11:30", notes:"DEMO: Interested in document verification software."},
    {id:2, name:"Shree Ganesh Co Op Credit Society Ltd.", phone:"9000000001", company:"Shree Ganesh Co Op Credit Society Ltd.", street:"Demo Market Road", village:"Shivajinagar", taluka:"Haveli", district:"Pune", state:"Maharashtra", pin:"411005", contacts:"Sunil Patil | Manager | 9000000001", referenceBy:"Amit Patil", referenceFrom:"Employee", branchCount:"5", priority:"High", owner:"Amit Patil", status:"Interested", followupDate:"2026-11-25T14:30", notes:"DEMO: Interested in automated loan processing."},
    {id:3, name:"Shree Nagari Sahakari Patsanstha Maryadit", phone:"9000000002", company:"Shree Nagari Sahakari Patsanstha Maryadit", street:"Sample College Road", village:"College Road", taluka:"Nashik", district:"Nashik", state:"Maharashtra", pin:"422005", contacts:"Priya Deshmukh | CEO | 9000000002\nRakesh Jadhav | IT Manager | 9000000003", referenceBy:"Neha Shah", referenceFrom:"Existing Institute", branchCount:"8", priority:"High", owner:"Neha Shah", status:"Follow-up", followupDate:"2026-11-26T15:00", notes:"DEMO: Asked for product demo and pricing."},
    {id:4, name:"Ganesh Chits Fund Pvt Ltd.", phone:"9000000004", company:"Ganesh Chits Fund Pvt Ltd.", street:"Example Main Road", village:"Tilakwadi", taluka:"Belgaum", district:"Belgaum", state:"Karnataka", pin:"590006", contacts:"Mahesh Kulkarni | Director | 9000000004", referenceBy:"Rohit More", referenceFrom:"WhatsApp", branchCount:"3", priority:"Medium", owner:"Rohit More", status:"Contacted", followupDate:"2026-11-27T12:00", notes:"DEMO: Introductory call completed; share brochure."},
    {id:5, name:"Aadhar Patpedhi Maryadit", phone:"9000000005", company:"Aadhar Patpedhi Maryadit", street:"Demo Station Road", village:"Dadar", taluka:"Mumbai City", district:"Mumbai", state:"Maharashtra", pin:"400014", contacts:"Kavita Shah | Branch Manager | 9000000005", referenceBy:"Amit Patil", referenceFrom:"Website", branchCount:"2", priority:"Low", owner:"Amit Patil", status:"New", followupDate:"2026-11-28T10:00", notes:"DEMO: New lead; make first introductory call."},
    {id:6, name:"Ganesha Multistate Credit Co Op Society Ltd.", phone:"9000000006", company:"Ganesha Multistate Credit Co Op Society Ltd.", street:"Sample Business Park", village:"Andheri East", taluka:"Andheri", district:"Mumbai Suburban", state:"Maharashtra", pin:"400069", contacts:"Vijay More | Operations Head | 9000000006\nSneha Rao | Accounts | 9000000007", referenceBy:"Neha Shah", referenceFrom:"Employee", branchCount:"12", priority:"High", owner:"Neha Shah", status:"Interested", followupDate:"2026-11-25T16:00", notes:"DEMO: Multiple branches; discuss centralized access."},
    {id:7, name:"Shree Sai Krupa Urban Co Op Credit Society Ltd.", phone:"9000000008", company:"Shree Sai Krupa Urban Co Op Credit Society Ltd.", street:"Example Ring Road", village:"Kothrud", taluka:"Haveli", district:"Pune", state:"Maharashtra", pin:"411038", contacts:"Anita Joshi | Secretary | 9000000008", referenceBy:"Sunil Patil", referenceFrom:"Existing Institute", branchCount:"4", priority:"Medium", owner:"Rohit More", status:"Contacted", followupDate:"2026-11-26T11:00", notes:"DEMO SAMPLE: Follow up after committee meeting."},
    {id:8, name:"Mahalaxmi Nagari Sahakari Patsanstha", phone:"9000000009", company:"Mahalaxmi Nagari Sahakari Patsanstha", street:"Demo Civil Lines", village:"Panchavati", taluka:"Nashik", district:"Nashik", state:"Maharashtra", pin:"422003", contacts:"Nitin Pawar | Chairman | 9000000009", referenceBy:"Priya Deshmukh", referenceFrom:"WhatsApp", branchCount:"6", priority:"High", owner:"Amit Patil", status:"Follow-up", followupDate:"2026-11-25T12:30", notes:"DEMO SAMPLE: Requested quotation."},
    {id:9, name:"Shree Samarth Multi State Credit Society Ltd.", phone:"9000000010", company:"Shree Samarth Multi State Credit Society Ltd.", street:"Sample Market Street", village:"Belagavi Camp", taluka:"Belgaum", district:"Belgaum", state:"Karnataka", pin:"590001", contacts:"Deepak Naik | Manager | 9000000010", referenceBy:"Rohit More", referenceFrom:"Other", branchCount:"9", priority:"Medium", owner:"Rohit More", status:"Interested", followupDate:"2026-11-29T14:00", notes:"DEMO SAMPLE: Wants details about branch-wise reports."},
    {id:10, name:"Aarohi Mahila Nagari Patsanstha", phone:"9000000011", company:"Aarohi Mahila Nagari Patsanstha", street:"Example Link Road", village:"Borivali West", taluka:"Borivali", district:"Mumbai Suburban", state:"Maharashtra", pin:"400092", contacts:"Meena Kulkarni | Administrator | 9000000011", referenceBy:"Kavita Shah", referenceFrom:"Website", branchCount:"1", priority:"Low", owner:"Neha Shah", status:"New", followupDate:"2026-11-30T10:30", notes:"DEMO SAMPLE: Single branch; initial contact pending."}
  ];

  const cityDistricts = [
    {district:"Pune", village:"Kothrud"}, {district:"Mumbai", village:"Dadar"},
    {district:"Nashik", village:"Panchavati"}, {district:"Nagpur", village:"Sitabuldi"},
    {district:"Chhatrapati Sambhajinagar", village:"Cidco"}, {district:"Kolhapur", village:"Rajarampuri"},
    {district:"Solapur", village:"South Sadar"}, {district:"Satara", village:"Wai"},
    {district:"Sangli", village:"Miraj"}, {district:"Thane", village:"Naupada"}
  ];

  const owners = ["Amit Patil", "Neha Shah", "Rohit More"];
  const statuses = ["Interested", "Follow-up", "Contacted", "New", "Converted"];

  for (let i = 11; i <= 60; i++) {
    const loc = cityDistricts[(i - 1) % cityDistricts.length];
    const own = owners[(i - 1) % owners.length];
    const st = statuses[(i - 1) % statuses.length];
    base.push({
      id: i,
      name: `Institute ${i} - Rural & Urban Cooperative Society`,
      phone: `98200000${i < 10 ? '0' + i : i}`,
      company: `Institute ${i} Cooperative Trust`,
      street: `Commercial Hub Road, Sector ${i % 10 + 1}`,
      village: loc.village,
      taluka: loc.district,
      district: loc.district,
      state: "Maharashtra",
      pin: `41${1000 + i}`,
      contacts: `Representative ${i} | Officer | 98200000${i < 10 ? '0' + i : i}`,
      referenceBy: own,
      referenceFrom: "Direct Referral",
      branchCount: String((i % 7) + 2),
      priority: i % 3 === 0 ? "High" : (i % 2 === 0 ? "Medium" : "Low"),
      owner: own,
      status: st,
      followupDate: `2026-11-${25 + (i % 5)}T${10 + (i % 6)}:00`,
      notes: `Institute active record #${i}`
    });
  }
  return base;
}

const state = {
  page: "dashboard",
  customers: generateInitialInstitutes(),
  calls: [
    {id:101, customer:"Shree Ganesh Co Op Credit Society Ltd.", type:"Outgoing", date:"25 Nov, 11:20", result:"Interested", notes:"DEMO: Asked for product demo."},
    {id:102, customer:"Shree Nagari Sahakari Patsanstha Maryadit", type:"Outgoing", date:"25 Nov, 10:10", result:"Call Back", notes:"DEMO: Call after committee meeting."},
    {id:103, customer:"Modern College Pune", type:"Incoming", date:"25 Nov, 09:40", result:"Interested", notes:"DEMO: Requested brochure and pricing."},
    {id:104, customer:"Aadhar Patpedhi Maryadit", type:"Outgoing", date:"24 Nov, 16:15", result:"No Answer", notes:"DEMO: Try again in the afternoon."}
  ],
  visits: [
    {id:201, customer:"Modern College Pune", date:"2026-11-25", owner:"Amit Patil", status:"Scheduled", purpose:"Software demo", notes:"DEMO: Bring laptop and demo credentials."},
    {id:202, customer:"Shree Ganesh Co Op Credit Society Ltd.", date:"2026-11-24", owner:"Rohit More", status:"Completed", purpose:"Introductory meeting", notes:"DEMO: Shared product overview."},
    {id:203, customer:"Ganesha Multistate Credit Co Op Society Ltd.", date:"2026-11-27", owner:"Neha Shah", status:"Scheduled", purpose:"Requirements discussion", notes:"DEMO: Discuss multi-branch needs."}
  ],
  followups: [
    {id:301, customer:"Modern College Pune", date:"2026-11-25T11:30", owner:"Amit Patil", status:"Due Today", notes:"DEMO: Document verification software demonstration."},
    {id:302, customer:"Shree Ganesh Co Op Credit Society Ltd.", date:"2026-11-25T14:30", owner:"Amit Patil", status:"Due Today", notes:"DEMO: Loan processing review."},
    {id:303, customer:"Shree Nagari Sahakari Patsanstha Maryadit", date:"2026-11-26T15:00", owner:"Neha Shah", status:"Upcoming", notes:"DEMO: Share pricing and quotation."},
    {id:304, customer:"Ganesha Multistate Credit Co Op Society Ltd.", date:"2026-11-25T16:00", owner:"Neha Shah", status:"Due Today", notes:"DEMO: Discuss multi-branch access."},
    {id:305, customer:"Ganesh Chits Fund Pvt Ltd.", date:"2026-11-24T12:00", owner:"Rohit More", status:"Overdue", notes:"DEMO: Send product brochure."}
  ],
  nextId: 500
};

// Ensure followups list has 20 pending items as seen in screenshot
for (let i = 6; i <= 21; i++) {
  state.followups.push({
    id: 300 + i,
    customer: state.customers[i]?.name || `Institute ${i}`,
    date: `2026-11-${25 + (i % 4)}T11:00`,
    owner: state.customers[i]?.owner || "Amit Patil",
    status: i % 4 === 0 ? "Overdue" : (i % 3 === 0 ? "Due Today" : "Upcoming"),
    notes: `Scheduled institute follow-up call #${i}`
  });
}

// LocalStorage Persistence
function loadLocalState() {
  try {
    const c = localStorage.getItem("crm_customers");
    if (c) {
      const parsed = JSON.parse(c);
      if (Array.isArray(parsed) && parsed.length > 0) state.customers = parsed;
    }
    const cl = localStorage.getItem("crm_calls");
    if (cl) {
      const parsed = JSON.parse(cl);
      if (Array.isArray(parsed) && parsed.length > 0) state.calls = parsed;
    }
    const v = localStorage.getItem("crm_visits");
    if (v) {
      const parsed = JSON.parse(v);
      if (Array.isArray(parsed) && parsed.length > 0) state.visits = parsed;
    }
    const f = localStorage.getItem("crm_followups");
    if (f) {
      const parsed = JSON.parse(f);
      if (Array.isArray(parsed) && parsed.length > 0) state.followups = parsed;
    }
  } catch(e) {}
}

function saveLocalState() {
  try {
    localStorage.setItem("crm_customers", JSON.stringify(state.customers));
    localStorage.setItem("crm_calls", JSON.stringify(state.calls));
    localStorage.setItem("crm_visits", JSON.stringify(state.visits));
    localStorage.setItem("crm_followups", JSON.stringify(state.followups));
  } catch(e) {}
}

loadLocalState();

const pageInfo = {
  dashboard: ["Dashboard", "Here’s what’s happening with your team today.", "Add Institute"],
  customers: ["Institutes", "Manage institute information, colleges and leads.", "Add Institute"],
  calls: ["Call Logs", "Track calls and their outcomes.", "Log Call"],
  visits: ["Visits", "Plan and record customer visits.", "Record Visit"],
  followups: ["Follow-ups", "Never miss your next customer follow-up.", "Schedule Follow-up"]
};

const employeeOptions = ["Unassigned", "Amit Patil", "Neha Shah", "Rohit More"];
const statusOptions = ["New", "Contacted", "Interested", "Follow-up", "Converted", "Not Interested"];
const priorityOptions = ["High", "Medium", "Low"];
const callResults = ["Interested", "Call Back", "No Answer", "Not Interested"];
const visitStatuses = ["Scheduled", "Completed", "Cancelled"];

function escapeHtml(value="") {
  if (value === null || value === undefined) return "";
  return String(value).replace(/[&<>"']/g, c => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
}

function initials(name="") {
  if (!name) return "IN";
  return String(name).split(/\s+/).filter(Boolean).slice(0,2).map(x=>x[0]).join("").toUpperCase() || "IN";
}

function statusClass(status="") {
  return "status-" + String(status || "new").toLowerCase().replace(/\s+/g,"-");
}

function badge(status) {
  return `<span class="status ${statusClass(status)}">${escapeHtml(status || "New")}</span>`;
}

function formatDate(date) {
  if (!date) return "—";
  const d = new Date(date);
  if (Number.isNaN(d.getTime())) return String(date);
  return d.toLocaleDateString("en-IN", {day:"2-digit", month:"short", year:"numeric"});
}

function formatDateTime(date) {
  if (!date) return "—";
  const d = new Date(date);
  if (Number.isNaN(d.getTime())) return String(date);
  return d.toLocaleDateString("en-IN", {day:"2-digit", month:"short"}) + " · " + d.toLocaleTimeString("en-IN", {hour:"2-digit", minute:"2-digit"});
}

function showNotice(message) {
  const n = $("notice");
  if (!n) return;
  n.textContent = message;
  n.classList.remove("hidden");
  window.clearTimeout(showNotice.timer);
  showNotice.timer = window.setTimeout(() => n.classList.add("hidden"), 4200);
}

function setPage(page) {
  if (!pageInfo[page]) return;
  state.page = page;
  document.querySelectorAll(".nav-link").forEach(b => b.classList.toggle("active", b.dataset.page === page));
  document.querySelectorAll(".page-panel").forEach(p => p.classList.add("hidden"));
  const activePanel = $(page + "-page");
  if (activePanel) activePanel.classList.remove("hidden");
  
  if ($("page-title")) $("page-title").textContent = pageInfo[page][0];
  if ($("breadcrumb-page")) $("breadcrumb-page").textContent = pageInfo[page][0];
  if ($("page-subtitle")) $("page-subtitle").textContent = pageInfo[page][1];
  if ($("primary-action-label")) $("primary-action-label").textContent = pageInfo[page][2];
  
  const r = activeRole();
  const primaryBtn = $("primary-action");
  if (primaryBtn) {
    const allowed = (state.page === "customers" && r.canAddInstitute) ||
                    (state.page === "calls" && r.canCall) ||
                    (state.page === "visits" && r.canVisit) ||
                    (state.page === "followups" && currentRole === "admin") ||
                    (state.page === "dashboard" && r.canAddInstitute);
    primaryBtn.classList.toggle("hidden", !allowed);
  }
  
  const sb = $("sidebar");
  if (sb) sb.classList.remove("open");
  renderAll();
}

function openDialog(type) {
  const titleMap = {customer:"Add Institute", call:"Log a Call", visit:"Record Customer Visit", followup:"Schedule Follow-up"};
  if ($("dialog-title")) $("dialog-title").textContent = titleMap[type] || "Add Record";
  if ($("dialog-fields")) $("dialog-fields").innerHTML = fieldsFor(type);
  const dlg = $("record-dialog");
  if (dlg) {
    dlg.dataset.type = type;
    dlg.showModal();
  }
}

function field(label, name, kind="text", opts=[], full=false, required=false, placeholder="") {
  let control = "";
  if (kind === "select") {
    control = `<select name="${name}" ${required?"required":""}>${opts.map(o=>`<option value="${escapeHtml(o.value??o)}">${escapeHtml(o.label??o)}</option>`).join("")}</select>`;
  } else if (kind === "textarea") {
    control = `<textarea name="${name}" placeholder="${escapeHtml(placeholder)}"></textarea>`;
  } else {
    const extraAttrs = name === "branchCount" ? ' min="1" step="1" inputmode="numeric"' : (name === "pin" ? ' maxlength="6" inputmode="numeric"' : "");
    control = `<input name="${name}" type="${kind}"${extraAttrs} ${required?"required":""} placeholder="${escapeHtml(placeholder)}">`;
  }
  return `<div class="form-field ${full?"full-width":""}"><label>${escapeHtml(label)}${required?" *":""}</label>${control}</div>`;
}

function fieldsFor(type) {
  const customerNames = visibleCustomers().map(c => ({value: c.name, label: c.name}));
  if (type === "customer") return [
    field("Institute Name", "name", "text", [], true, true, "e.g. Modern College Pune"),
    field("Street / Area", "street", "text", [], true, false, "e.g. Shivajinagar"),
    field("Gaon / Village", "village", "text", [], false, false, "e.g. Shivajinagar"),
    field("Taluka", "taluka", "text", [], false, false, "e.g. Haveli"),
    field("District", "district", "text", [], false, false, "e.g. Pune"),
    field("State", "state", "text", [], false, false, "e.g. Maharashtra"),
    field("PIN Code", "pin", "text", [], false, false, "e.g. 411005 (6 digits)"),
    field("Contact Person(s)", "contacts", "textarea", [], true, false, "Name | Position | Contact No.\nExample: Dr. Sanjay Kharat | Principal | 9822011223"),
    field("Reference By", "referenceBy", "text", [], false, false, "e.g. Amit Patil"),
    field("Reference From", "referenceFrom", "select", ["Select source","Employee","Existing Institute","Website","WhatsApp","Other"].map(x=>({value:x==="Select source"?"":x, label:x}))),
    field("No. of Branches", "branchCount", "number", [], false, false, "e.g. 3"),
    field("Priority", "priority", "select", priorityOptions.map(x=>({value:x,label:x}))),
    field("Assigned Employee", "owner", "select", employeeOptions.map(x=>({value:x,label:x})), false, true),
    field("Lead Status", "status", "select", statusOptions.map(x=>({value:x,label:x})), false, true),
    field("Next Follow-up Date & Time", "followupDate", "datetime-local", [], true),
    field("Notes", "notes", "textarea", [], true, false, "e.g. Interested in software demo; asked for quotation")
  ].join("");

  if (type === "call") return [
    field("Institute", "customer", "select", customerNames, true, true),
    field("Call type", "callType", "select", ["Outgoing","Incoming"].map(x=>({value:x,label:x}))),
    field("Call date & time", "activityDate", "datetime-local", [], false, true),
    field("Employee who called", "owner", "select", employeeOptions.map(x=>({value:x,label:x})), false, true),
    field("Contact person called", "contactPerson", "text", [], false, false, "e.g. Dr. Sanjay Kharat"),
    field("Contact number", "contactPhone", "tel", [], false, false, "e.g. 9822011223"),
    field("Call outcome", "result", "select", callResults.map(x=>({value:x,label:x})), false, true),
    field("Next follow-up date & time", "followupDate", "datetime-local"),
    field("Call notes", "notes", "textarea", [], true, false, "What was discussed? What happens next?")
  ].join("");

  if (type === "visit") return [
    field("Institute / company", "customer", "select", customerNames, true, true),
    field("Visit date & time", "activityDate", "datetime-local", [], false, true),
    field("Contact person met", "contactPerson", "text", [], false, false, "e.g. Dr. Sanjay Kharat"),
    field("Contact number", "contactPhone", "tel", [], false, false, "e.g. 9822011223"),
    field("Assigned employee", "owner", "select", employeeOptions.map(x=>({value:x,label:x}))),
    field("Visit status", "status", "select", visitStatuses.map(x=>({value:x,label:x}))),
    field("Purpose", "purpose", "text", [], true, false, "Purpose of visit"),
    field("Visit notes", "notes", "textarea", [], true, false, "Visit outcome and next steps")
  ].join("");

  return [
    field("Institute", "customer", "select", customerNames, true, true),
    field("Follow-up date & time", "date", "datetime-local", [], true, true),
    field("Assigned employee", "owner", "select", employeeOptions.map(x=>({value:x,label:x}))),
    field("Notes", "notes", "textarea", [], true, false, "What needs to be discussed?")
  ].join("");
}

function customerByName(name) { return state.customers.find(c => c.name === name); }

function renderCustomers() {
  const searchInput = $("customer-search");
  const filterInput = $("customer-status-filter");
  const q = (searchInput ? searchInput.value : "").toLowerCase();
  const status = filterInput ? filterInput.value : "";
  
  const rows = visibleCustomers().filter(c => 
    (`${c.name} ${c.phone} ${c.owner} ${c.district||""} ${c.state||""} ${c.village||""} ${c.street||""} ${c.contacts||""}`).toLowerCase().includes(q) &&
    (!status || c.status === status)
  );

  const countEl = $("customer-record-count");
  if (countEl) countEl.textContent = `${rows.length} record${rows.length===1?"":"s"}`;

  const tableEl = $("customers-table");
  if (tableEl) {
    tableEl.innerHTML = rows.map(c => `<tr>
      <td><div class="customer-cell"><div class="mini-avatar">${escapeHtml(initials(c.name))}</div><div><button class="institute-link" data-open-institute="${escapeHtml(c.name)}">${escapeHtml(c.name)}</button><small>${escapeHtml(c.contacts||"No contact person added")}</small></div></div></td>
      <td>${escapeHtml([c.village, c.taluka, c.district, c.state].filter(Boolean).join(", ") || c.street || "—")}</td>
      <td>${escapeHtml(c.phone || "—")}</td>
      <td>${escapeHtml(c.branchCount || "—")}</td>
      <td>${escapeHtml(c.owner || "Unassigned")}</td>
      <td>${badge(c.priority || "Medium")}</td>
      <td>${badge(c.status)}</td>
      <td>${escapeHtml(c.followupDate ? formatDateTime(c.followupDate) : "—")}</td>
      <td><button class="table-action" data-log-customer="${escapeHtml(c.name)}">Log call</button></td>
    </tr>`).join("") || `<tr><td colspan="9"><div class="empty-state">No institutes found.</div></td></tr>`;
  }

  const mobileEl = $("customers-mobile");
  if (mobileEl) {
    mobileEl.innerHTML = rows.map(c => `<div class="mobile-record">
      <div class="mobile-record-top"><div class="mini-avatar">${escapeHtml(initials(c.name))}</div><div><button class="institute-link" data-open-institute="${escapeHtml(c.name)}">${escapeHtml(c.name)}</button><small>${escapeHtml([c.village, c.taluka, c.district].filter(Boolean).join(", ") || "Address not added")}</small></div>${badge(c.status)}</div>
      <div class="mobile-record-meta"><span>☎ ${escapeHtml(c.phone||"No contact")}</span><span>👤 ${escapeHtml(c.owner||"Unassigned")}</span><span>Branches: ${escapeHtml(c.branchCount||"1")}</span></div>
      <div class="mobile-record-actions"><button class="table-action" data-log-customer="${escapeHtml(c.name)}">Log call</button></div>
    </div>`).join("") || `<div class="empty-state">No institutes found.</div>`;
  }
}

function renderCalls() {
  const q = ($("call-search")?.value || "").toLowerCase();
  const result = $("call-result-filter")?.value || "";
  const rows = state.calls.filter(c => visibleNames().has(c.customer) && (`${c.customer} ${c.result} ${c.notes} ${c.date}`).toLowerCase().includes(q) && (!result || c.result === result));
  
  if ($("call-record-count")) $("call-record-count").textContent = `${rows.length} log${rows.length===1?"":"s"}`;
  
  if ($("calls-table")) {
    $("calls-table").innerHTML = rows.map(c => `<tr>
      <td><div class="customer-cell"><div class="mini-avatar">${escapeHtml(initials(c.customer))}</div><div><button class="institute-link" data-open-institute="${escapeHtml(c.customer)}">${escapeHtml(c.customer)}</button></div></div></td>
      <td>${escapeHtml(c.type)}</td>
      <td>${escapeHtml(c.date)}</td>
      <td>${badge(c.result)}</td>
      <td>${escapeHtml(c.notes || "—")}</td>
    </tr>`).join("") || `<tr><td colspan="5"><div class="empty-state">No call logs found.</div></td></tr>`;
  }

  if ($("calls-mobile")) {
    $("calls-mobile").innerHTML = rows.map(c => `<div class="mobile-record">
      <div class="mobile-record-top"><div class="mini-avatar">☎</div><div><button class="institute-link" data-open-institute="${escapeHtml(c.customer)}">${escapeHtml(c.customer)}</button><small>${escapeHtml(c.date)} · ${escapeHtml(c.type)}</small></div>${badge(c.result)}</div>
      <div class="mobile-record-meta"><span>${escapeHtml(c.notes || "No notes")}</span></div>
    </div>`).join("") || `<div class="empty-state">No call logs found.</div>`;
  }
}

function renderVisits() {
  const q = ($("visit-search")?.value || "").toLowerCase();
  const status = $("visit-status-filter")?.value || "";
  const rows = state.visits.filter(v => visibleNames().has(v.customer) && (`${v.customer} ${v.owner} ${v.purpose}`).toLowerCase().includes(q) && (!status || v.status === status));
  
  if ($("visit-record-count")) $("visit-record-count").textContent = `${rows.length} visit${rows.length===1?"":"s"}`;
  
  if ($("visits-table")) {
    $("visits-table").innerHTML = rows.map(v => `<tr>
      <td><div class="customer-cell"><div class="mini-avatar">⌖</div><div><button class="institute-link" data-open-institute="${escapeHtml(v.customer)}">${escapeHtml(v.customer)}</button><small>${escapeHtml(v.purpose||"Customer visit")}</small></div></div></td>
      <td>${escapeHtml(formatDate(v.date))}</td>
      <td>${escapeHtml(v.owner)}</td>
      <td>${badge(v.status)}</td>
      <td>${v.status === "Scheduled" ? `<button class="table-action" data-complete-visit="${v.id}">Mark completed</button>` : "—"}</td>
    </tr>`).join("") || `<tr><td colspan="5"><div class="empty-state">No visit records found.</div></td></tr>`;
  }

  if ($("visits-mobile")) {
    $("visits-mobile").innerHTML = rows.map(v => `<div class="mobile-record">
      <div class="mobile-record-top"><div class="mini-avatar">⌖</div><div><button class="institute-link" data-open-institute="${escapeHtml(v.customer)}">${escapeHtml(v.customer)}</button><small>${escapeHtml(formatDate(v.date))}</small></div>${badge(v.status)}</div>
      <div class="mobile-record-meta"><span>${escapeHtml(v.owner)}</span><span>${escapeHtml(v.purpose||"Customer visit")}</span></div>
      ${v.status === "Scheduled" ? `<div class="mobile-record-actions"><button class="table-action" data-complete-visit="${v.id}">Mark completed</button></div>` : ""}
    </div>`).join("") || `<div class="empty-state">No visit records found.</div>`;
  }
}

function renderFollowups() {
  const q = ($("followup-search")?.value || "").toLowerCase();
  const filter = $("followup-filter")?.value || "";
  const rows = state.followups.filter(f => visibleNames().has(f.customer) && (`${f.customer} ${f.owner} ${f.notes} ${f.status}`).toLowerCase().includes(q) && (!filter || f.status === filter));
  
  if ($("due-count")) $("due-count").textContent = state.followups.filter(f => visibleNames().has(f.customer) && f.status === "Due Today").length;
  if ($("overdue-count")) $("overdue-count").textContent = state.followups.filter(f => visibleNames().has(f.customer) && f.status === "Overdue").length;
  if ($("upcoming-count")) $("upcoming-count").textContent = state.followups.filter(f => visibleNames().has(f.customer) && f.status === "Upcoming").length;
  if ($("followup-count")) $("followup-count").textContent = state.followups.filter(f => visibleNames().has(f.customer) && (f.status === "Due Today" || f.status === "Overdue")).length;

  if ($("followups-list")) {
    $("followups-list").innerHTML = rows.map(f => `<div class="followup-card ${f.status==="Overdue"?"is-overdue":""}">
      <div class="followup-date-icon">◷</div>
      <div class="followup-details">
        <strong><button class="institute-link" data-open-institute="${escapeHtml(f.customer)}">${escapeHtml(f.customer)}</button> ${badge(f.status)}</strong>
        <p>${escapeHtml(formatDateTime(f.date))} · ${escapeHtml(f.owner)}</p>
        <p>${escapeHtml(f.notes || "No notes")}</p>
      </div>
      <div class="followup-actions">
        ${f.status !== "Completed" ? `<button class="small-button" data-complete-followup="${f.id}">✓ Complete</button><button class="small-button" data-reschedule-followup="${f.id}">Reschedule</button><button class="small-button" data-followup-call="${escapeHtml(f.customer)}">Log call</button>` : `<span class="pill pill-blue">Done</span>`}
      </div>
    </div>`).join("") || `<div class="empty-state">No follow-ups found.</div>`;
  }
}

function renderDashboard() {
  // Stat cards matching the screenshot: Total Institutes: 60, Calls: 4, Visits: 3, Pending: 20
  if ($("stat-customers")) $("stat-customers").textContent = visibleCustomers().length;
  if ($("stat-calls")) $("stat-calls").textContent = state.calls.filter(c => visibleNames().has(c.customer)).length;
  if ($("stat-visits")) $("stat-visits").textContent = state.visits.filter(v => visibleNames().has(v.customer)).length;
  if ($("stat-followups")) $("stat-followups").textContent = state.followups.filter(f => visibleNames().has(f.customer) && f.status !== "Completed").length;

  // Follow-ups to focus on
  const pending = state.followups.filter(f => visibleNames().has(f.customer) && f.status !== "Completed").slice(0, 4);
  if ($("dashboard-followups")) {
    $("dashboard-followups").innerHTML = pending.map(f => `<div class="activity-row">
      <div class="mini-avatar">${escapeHtml(initials(f.customer))}</div>
      <div class="activity-main"><button class="institute-link" data-open-institute="${escapeHtml(f.customer)}">${escapeHtml(f.customer)}</button><small>${escapeHtml(formatDateTime(f.date))} · ${escapeHtml(f.owner)}</small></div>
      <div class="activity-side">${badge(f.status)}</div>
    </div>`).join("") || `<div class="empty-state">All follow-ups completed.</div>`;
  }

  // Recent call activity
  const recentCalls = state.calls.filter(c => visibleNames().has(c.customer)).slice(0, 4);
  if ($("dashboard-calls")) {
    $("dashboard-calls").innerHTML = recentCalls.map(c => `<div class="activity-row">
      <div class="mini-avatar">☎</div>
      <div class="activity-main"><button class="institute-link" data-open-institute="${escapeHtml(c.customer)}">${escapeHtml(c.customer)}</button><small>${escapeHtml(c.date)} · ${escapeHtml(c.type)}</small></div>
      <div class="activity-side">${badge(c.result)}</div>
    </div>`).join("") || `<div class="empty-state">No calls logged yet.</div>`;
  }
}

function openInstitute(name) {
  const c = customerByName(name);
  if (!c) return;
  const calls = state.calls.filter(x => x.customer === name).map(x => ({kind:"Call", date:x.date, employee:x.owner||"Amit Patil", contact:x.contactPerson||"Not recorded", phone:x.contactPhone||c.phone||"Not recorded", summary:`${x.type||"Call"} · ${x.result||"No outcome"}${x.notes?" — "+x.notes:""}`}));
  const visits = state.visits.filter(x => x.customer === name).map(x => ({kind:"Visit", date:x.date, employee:x.owner||"Amit Patil", contact:x.contactPerson||"Not recorded", phone:x.contactPhone||c.phone||"Not recorded", summary:`${x.purpose||"Institute visit"} · ${x.status||""}${x.notes?" — "+x.notes:""}`}));
  const activity = [...calls, ...visits].sort((a,b) => new Date(b.date) - new Date(a.date));
  const address = [c.street, c.village, c.taluka, c.district, c.state, c.pin].filter(Boolean).join(", ");
  
  const contentEl = $("institute-detail-content");
  if (contentEl) {
    contentEl.innerHTML = `
      <div class="detail-top">
        <div><p class="eyebrow">INSTITUTE PROFILE</p><h2>${escapeHtml(c.name)}</h2><p>${escapeHtml(address || "Address not added")}</p></div>
        <div class="detail-actions">
          <button class="btn btn-secondary ${activeRole().canCall?"":"hidden"}" data-detail-call="${escapeHtml(c.name)}">+ Add Call</button>
          <button class="btn btn-primary ${activeRole().canVisit?"":"hidden"}" data-detail-visit="${escapeHtml(c.name)}">+ Add Visit</button>
        </div>
      </div>
      <div class="detail-grid">
        <div class="detail-field"><small>Contact Person(s)</small><strong>${escapeHtml(c.contacts||"Not added").replace(/\n/g,"<br>")}</strong></div>
        <div class="detail-field"><small>Contact Number</small><strong>${escapeHtml(c.phone||"Not added")}</strong></div>
        <div class="detail-field"><small>Reference By / From</small><strong>${escapeHtml(c.referenceBy||"—")} / ${escapeHtml(c.referenceFrom||"—")}</strong></div>
        <div class="detail-field"><small>No. of Branches</small><strong>${escapeHtml(c.branchCount||"Not entered")}</strong></div>
        <div class="detail-field"><small>Assigned Employee</small><strong>${escapeHtml(c.owner||"Unassigned")}</strong></div>
        <div class="detail-field"><small>Priority / Lead Status</small><strong>${escapeHtml(c.priority||"Medium")} / ${escapeHtml(c.status||"New")}</strong></div>
        <div class="detail-field"><small>Next Follow-up</small><strong>${escapeHtml(c.followupDate?formatDateTime(c.followupDate):"Not scheduled")}</strong></div>
        <div class="detail-field"><small>Notes</small><strong>${escapeHtml(c.notes||"—")}</strong></div>
      </div>
      <div class="detail-history-heading">
        <div><h3>Call & Visit History</h3><p>Employee, date/time, contact person and discussion summary</p></div>
        <span class="pill pill-blue">${activity.length} records</span>
      </div>
      <div class="detail-history">
        ${activity.map(a => `<article class="history-item">
          <div class="history-kind">${escapeHtml(a.kind)}</div>
          <div class="history-main">
            <strong>${escapeHtml(a.date)} · ${escapeHtml(a.employee)}</strong>
            <p><b>Contact:</b> ${escapeHtml(a.contact)} · ${escapeHtml(a.phone)}</p>
            <p><b>Discussion:</b> ${escapeHtml(a.summary)}</p>
          </div>
        </article>`).join("") || '<div class="empty-state">No calls or visits recorded yet.</div>'}
      </div>
    `;
    const dlg = $("institute-detail-dialog");
    if (dlg) dlg.showModal();
  }
}

function renderAll() {
  renderDashboard();
  renderCustomers();
  renderCalls();
  renderVisits();
  renderFollowups();
}

// Save Record (Local & Sync to Spring Boot Backend)
function saveRecord(type, data) {
  const now = new Date();
  if (type === "customer") {
    const primaryPhone = (data.contacts || "").split("\n").map(line => line.split("|").map(p => p.trim())).find(p => p[2])?.[2] || data.phone || "";
    const customer = {
      id: state.nextId++,
      name: data.name.trim(),
      phone: primaryPhone,
      company: data.name.trim(),
      street: data.street?.trim() || "",
      village: data.village?.trim() || "",
      taluka: data.taluka?.trim() || "",
      district: data.district?.trim() || "",
      state: data.state?.trim() || "Maharashtra",
      pin: data.pin?.trim() || "",
      contacts: data.contacts?.trim() || "",
      referenceBy: data.referenceBy?.trim() || activeRole().user,
      referenceFrom: data.referenceFrom || "Website",
      branchCount: data.branchCount || "1",
      priority: data.priority || "Medium",
      owner: data.owner || activeRole().user,
      status: data.status || "New",
      followupDate: data.followupDate || "",
      notes: data.notes?.trim() || ""
    };
    state.customers.unshift(customer);
    if (data.followupDate) {
      state.followups.unshift({
        id: state.nextId++,
        customer: customer.name,
        date: data.followupDate,
        owner: customer.owner,
        status: "Upcoming",
        notes: data.notes?.trim() || "Institute follow-up"
      });
    }
    saveLocalState();
    showNotice("✓ Institute saved successfully!");

    // Sync to Backend
    fetch(`${API_URL}/institutes`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        instituteName: customer.name,
        primaryContactPhone: customer.phone,
        streetAddress: customer.street,
        village: customer.village,
        taluka: customer.taluka,
        district: customer.district,
        state: customer.state,
        pinCode: customer.pin,
        priority: customer.priority,
        leadStatus: customer.status,
        notes: customer.notes
      })
    }).catch(() => {});

  } else if (type === "call") {
    const actDate = data.activityDate ? formatDateTime(data.activityDate) : "25 Nov, " + now.toLocaleTimeString([], {hour:'2-digit', minute:'2-digit'});
    state.calls.unshift({
      id: state.nextId++,
      customer: data.customer,
      type: data.callType || "Outgoing",
      date: actDate,
      owner: data.owner || activeRole().user,
      contactPerson: data.contactPerson?.trim() || "",
      contactPhone: data.contactPhone?.trim() || "",
      result: data.result || "Interested",
      notes: data.notes?.trim() || ""
    });
    if (data.followupDate) {
      state.followups.unshift({
        id: state.nextId++,
        customer: data.customer,
        date: data.followupDate,
        owner: data.owner || activeRole().user,
        status: "Upcoming",
        notes: "Follow-up after call"
      });
    }
    saveLocalState();
    showNotice("✓ Call log saved successfully!");

    // Sync to Backend
    fetch(`${API_URL}/calls`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        customer: data.customer,
        type: data.callType,
        result: data.result,
        notes: data.notes
      })
    }).catch(() => {});

  } else if (type === "visit") {
    const actDate = data.activityDate ? data.activityDate.slice(0, 10) : now.toISOString().slice(0, 10);
    state.visits.unshift({
      id: state.nextId++,
      customer: data.customer,
      date: actDate,
      owner: data.owner || activeRole().user,
      status: data.status || "Scheduled",
      contactPerson: data.contactPerson?.trim() || "",
      contactPhone: data.contactPhone?.trim() || "",
      purpose: data.purpose?.trim() || "Customer visit",
      notes: data.notes?.trim() || ""
    });
    saveLocalState();
    showNotice("✓ Visit record saved successfully!");

    // Sync to Backend
    fetch(`${API_URL}/visits`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        customer: data.customer,
        date: actDate,
        purpose: data.purpose,
        status: data.status,
        notes: data.notes
      })
    }).catch(() => {});

  } else if (type === "followup") {
    state.followups.unshift({
      id: state.nextId++,
      customer: data.customer,
      date: data.date,
      owner: data.owner || activeRole().user,
      status: "Upcoming",
      notes: data.notes?.trim() || ""
    });
    saveLocalState();
    showNotice("✓ Follow-up scheduled successfully!");

    // Sync to Backend
    fetch(`${API_URL}/follow-ups`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        customer: data.customer,
        scheduledAt: data.date,
        notes: data.notes
      })
    }).catch(() => {});
  }

  const dlg = $("record-dialog");
  if (dlg) dlg.close();
  renderAll();
}

// Background sync from backend
async function loadLiveBackendData() {
  try {
    const res = await fetch(`${API_URL}/institutes`, { signal: AbortSignal.timeout(2500) });
    if (res.ok) {
      const json = await res.json();
      const list = json.data || json;
      if (Array.isArray(list) && list.length > 0) {
        list.forEach(inst => {
          const name = inst.instituteName || inst.name;
          if (name && !state.customers.some(c => c.name.toLowerCase() === name.toLowerCase())) {
            state.customers.unshift({
              id: inst.instituteId || inst.id || state.nextId++,
              name: name,
              phone: inst.primaryContactPhone || inst.phone || "",
              company: name,
              street: inst.streetAddress || "",
              village: inst.village || "",
              taluka: inst.taluka || "",
              district: inst.district || "",
              state: inst.state || "Maharashtra",
              pin: inst.pinCode || "",
              contacts: inst.contactPersons || "",
              referenceBy: inst.referenceBy || "Amit Patil",
              referenceFrom: inst.referenceFrom || "Employee",
              branchCount: inst.numberOfBranches ? String(inst.numberOfBranches) : "1",
              priority: inst.priority || "Medium",
              owner: inst.assignedEmployeeName || "Amit Patil",
              status: inst.leadStatus || "New",
              followupDate: inst.nextFollowUpAt ? inst.nextFollowUpAt.slice(0, 16) : "",
              notes: inst.notes || ""
            });
          }
        });
        saveLocalState();
        renderAll();
      }
    }
  } catch(e) {}
}

// ==============================================================
// LOGIN / LOGOUT & AUTH AUDIT MANAGEMENT
// ==============================================================
function fillRole(email, pwd) {
  if ($("input-email")) $("input-email").value = email;
  if ($("input-password")) $("input-password").value = pwd;
  showLoginMsg("Credentials filled! Click 'Sign In to Workspace' below.", "ok");
}

function showLoginMsg(msg, type = "err") {
  const box = $("login-status-msg");
  if (!box) return;
  box.textContent = msg;
  box.className = "login-msg " + type;
}

function checkAuthSession() {
  const token = localStorage.getItem("crm_access_token");
  const userStr = localStorage.getItem("crm_auth_user");

  if (token && userStr) {
    try {
      const user = JSON.parse(userStr);
      renderDashboardForUser(user);
      return;
    } catch(e) {
      localStorage.removeItem("crm_auth_user");
      localStorage.removeItem("crm_access_token");
    }
  }

  // Not logged in -> Show login screen
  if ($("login-overlay")) $("login-overlay").style.display = "flex";
  if ($("crm-app-shell")) $("crm-app-shell").style.display = "none";
}

function renderDashboardForUser(user) {
  if ($("login-overlay")) $("login-overlay").style.display = "none";
  if ($("crm-app-shell")) $("crm-app-shell").style.display = "flex";

  const email = (user.email || "").toLowerCase();
  if (email.includes("neha") || (user.role && user.role.includes("Calling"))) {
    currentRole = "calling";
  } else if (email.includes("rohit") || (user.role && user.role.includes("Field"))) {
    currentRole = "field";
  } else {
    currentRole = "admin";
  }

  const roleObj = activeRole();
  const displayName = user.fullName || roleObj.user;
  const displayRole = roleObj.label;
  const displayInitials = roleObj.initials;

  if ($("current-user-name")) $("current-user-name").textContent = displayName;
  if ($("current-user-role")) $("current-user-role").textContent = displayRole;
  if ($("current-user-avatar")) $("current-user-avatar").textContent = displayInitials;
  if ($("sidebar-user-avatar")) $("sidebar-user-avatar").textContent = displayInitials;

  const roleBanner = $("role-banner");
  if (roleBanner) {
    roleBanner.textContent = currentRole === "admin"
      ? "Administrative role: full access to all institutes and activities."
      : `${displayRole}: showing only institutes assigned to ${displayName}.`;
  }

  applyRole();
  loadLiveBackendData();
}

// Record Audit Log to local array & backend
function recordAuditLog(type, email, fullName, roleName) {
  const logsStr = localStorage.getItem("crm_audit_logs") || "[]";
  let logs = [];
  try { logs = JSON.parse(logsStr); } catch(e) { logs = []; }

  const now = new Date();
  if (type === "LOGIN") {
    logs.unshift({
      id: Date.now(),
      fullName: fullName,
      email: email,
      roleName: roleName,
      loginTime: now.toISOString(),
      logoutTime: null,
      status: "LOGGED_IN",
      ipAddress: "127.0.0.1"
    });
  } else if (type === "LOGOUT") {
    const activeLog = logs.find(l => l.email === email && !l.logoutTime);
    if (activeLog) {
      activeLog.logoutTime = now.toISOString();
      activeLog.status = "LOGGED_OUT";
    } else {
      logs.unshift({
        id: Date.now(),
        fullName: fullName,
        email: email,
        roleName: roleName,
        loginTime: new Date(now.getTime() - 1000 * 60 * 30).toISOString(),
        logoutTime: now.toISOString(),
        status: "LOGGED_OUT",
        ipAddress: "127.0.0.1"
      });
    }
  }
  localStorage.setItem("crm_audit_logs", JSON.stringify(logs.slice(0, 30)));
}

// DOM Setup & Listeners
window.addEventListener("DOMContentLoaded", () => {
  // Today label
  const todayEl = $("today-label");
  if (todayEl) {
    todayEl.textContent = new Date().toLocaleDateString("en-IN", {weekday:"short", day:"2-digit", month:"short"});
  }

  // Form submit for dialog
  const recForm = $("record-form");
  if (recForm) {
    recForm.addEventListener("submit", e => {
      e.preventDefault();
      const type = $("record-dialog").dataset.type;
      const data = Object.fromEntries(new FormData(e.currentTarget).entries());
      if (type === "customer" && !data.name?.trim()) {
        showNotice("Institute name is required.");
        return;
      }
      if (type === "customer" && data.pin?.trim() && !/^\d{6}$/.test(data.pin.trim())) {
        showNotice("Please enter a valid 6-digit PIN code.");
        return;
      }
      saveRecord(type, data);
    });
  }

  // Primary action button (+ Add Institute)
  const primBtn = $("primary-action");
  if (primBtn) {
    primBtn.addEventListener("click", () => {
      openDialog(({dashboard:"customer", customers:"customer", calls:"call", visits:"visit", followups:"followup"})[state.page] || "customer");
    });
  }

  // Dialog Close buttons
  if ($("close-dialog")) $("close-dialog").addEventListener("click", () => $("record-dialog").close());
  if ($("cancel-dialog")) $("cancel-dialog").addEventListener("click", () => $("record-dialog").close());
  if ($("close-detail-dialog")) $("close-detail-dialog").addEventListener("click", () => $("institute-detail-dialog").close());
  if ($("mobile-menu")) $("mobile-menu").addEventListener("click", () => $("sidebar").classList.toggle("open"));

  // Navigation Links
  document.querySelectorAll(".nav-link").forEach(b => {
    b.addEventListener("click", () => setPage(b.dataset.page));
  });

  // Role switcher dropdown (if visible)
  const roleSelect = $("role-switcher");
  if (roleSelect) {
    roleSelect.addEventListener("change", e => {
      currentRole = e.target.value;
      applyRole();
      showNotice(`Demo role switched to ${activeRole().label}.`);
    });
  }

  document.querySelectorAll("[data-goto]").forEach(b => {
    b.addEventListener("click", () => setPage(b.dataset.goto));
  });

  // Search & Filter listeners
  ["customer-search", "customer-status-filter"].forEach(id => {
    const el = $(id);
    if (el) el.addEventListener("input", renderCustomers);
  });
  ["call-search", "call-result-filter"].forEach(id => {
    const el = $(id);
    if (el) el.addEventListener("input", renderCalls);
  });
  ["visit-search", "visit-status-filter"].forEach(id => {
    const el = $(id);
    if (el) el.addEventListener("input", renderVisits);
  });
  ["followup-search", "followup-filter"].forEach(id => {
    const el = $(id);
    if (el) el.addEventListener("input", renderFollowups);
  });

  // Event delegation for clickable table links and action buttons
  document.addEventListener("click", e => {
    const institute = e.target.closest("[data-open-institute]");
    if (institute) { openInstitute(institute.dataset.openInstitute); return; }

    const detailCall = e.target.closest("[data-detail-call]");
    if (detailCall) {
      const name = detailCall.dataset.detailCall;
      $("institute-detail-dialog").close();
      openDialog("call");
      const f = $("dialog-fields");
      if (f) {
        if (f.querySelector('[name="customer"]')) f.querySelector('[name="customer"]').value = name;
        const c = customerByName(name);
        if (c) {
          const parts = (c.contacts||"").split("\n")[0].split("|").map(x => x.trim());
          if (f.querySelector('[name="contactPerson"]')) f.querySelector('[name="contactPerson"]').value = parts[0] || "";
          if (f.querySelector('[name="contactPhone"]')) f.querySelector('[name="contactPhone"]').value = parts[2] || c.phone || "";
          if (f.querySelector('[name="owner"]')) f.querySelector('[name="owner"]').value = c.owner || activeRole().user;
        }
      }
      return;
    }

    const detailVisit = e.target.closest("[data-detail-visit]");
    if (detailVisit) {
      const name = detailVisit.dataset.detailVisit;
      $("institute-detail-dialog").close();
      openDialog("visit");
      const f = $("dialog-fields");
      if (f) {
        if (f.querySelector('[name="customer"]')) f.querySelector('[name="customer"]').value = name;
        const c = customerByName(name);
        if (c) {
          const parts = (c.contacts||"").split("\n")[0].split("|").map(x => x.trim());
          if (f.querySelector('[name="contactPerson"]')) f.querySelector('[name="contactPerson"]').value = parts[0] || "";
          if (f.querySelector('[name="contactPhone"]')) f.querySelector('[name="contactPhone"]').value = parts[2] || c.phone || "";
          if (f.querySelector('[name="owner"]')) f.querySelector('[name="owner"]').value = c.owner || activeRole().user;
        }
      }
      return;
    }

    const log = e.target.closest("[data-log-customer]");
    if (log) {
      setPage("calls");
      openDialog("call");
      const select = $('dialog-fields')?.querySelector('[name="customer"]');
      if (select) select.value = log.dataset.logCustomer;
      return;
    }

    const completeVisit = e.target.closest("[data-complete-visit]");
    if (completeVisit) {
      const v = state.visits.find(x => x.id === Number(completeVisit.dataset.completeVisit));
      if (v) v.status = "Completed";
      saveLocalState();
      renderAll();
      showNotice("✓ Visit marked as completed.");
      return;
    }

    const complete = e.target.closest("[data-complete-followup]");
    if (complete) {
      const f = state.followups.find(x => x.id === Number(complete.dataset.completeFollowup));
      if (f) f.status = "Completed";
      saveLocalState();
      renderAll();
      showNotice("✓ Follow-up marked as completed.");
      return;
    }

    const reschedule = e.target.closest("[data-reschedule-followup]");
    if (reschedule) {
      const f = state.followups.find(x => x.id === Number(reschedule.dataset.rescheduleFollowup));
      if (!f) return;
      openDialog("followup");
      const fields = $("dialog-fields");
      if (fields) {
        if (fields.querySelector('[name="customer"]')) fields.querySelector('[name="customer"]').value = f.customer;
        if (fields.querySelector('[name="owner"]')) fields.querySelector('[name="owner"]').value = f.owner;
        if (fields.querySelector('[name="date"]')) fields.querySelector('[name="date"]').value = f.date;
        if (fields.querySelector('[name="notes"]')) fields.querySelector('[name="notes"]').value = f.notes || "";
      }
      state.followups = state.followups.filter(x => x.id !== f.id);
      saveLocalState();
      return;
    }

    const logFollowup = e.target.closest("[data-followup-call]");
    if (logFollowup) {
      setPage("calls");
      openDialog("call");
      const select = $('dialog-fields')?.querySelector('[name="customer"]');
      if (select) select.value = logFollowup.dataset.followupCall;
      return;
    }
  });

  // Login Form Submission
  const authForm = $("crm-auth-form");
  if (authForm) {
    authForm.addEventListener("submit", async e => {
      e.preventDefault();
      const btn = $("btn-signin-submit");
      if (btn) {
        btn.disabled = true;
        btn.textContent = "Verifying...";
      }
      if ($("login-status-msg")) $("login-status-msg").style.display = "none";

      const email = $("input-email")?.value.trim() || "";
      const password = $("input-password")?.value || "";

      const fallbackStaff = {
        "amit.patil@example.com": { fullName: "Amit Patil", email: "amit.patil@example.com", role: "Administrative", accessToken: "token_amit_admin" },
        "neha.shah@example.com": { fullName: "Neha Shah", email: "neha.shah@example.com", role: "Calling Team", accessToken: "token_neha_calling" },
        "rohit.more@example.com": { fullName: "Rohit More", email: "rohit.more@example.com", role: "On-field Employee", accessToken: "token_rohit_field" }
      };

      try {
        const res = await fetch(`${API_URL}/auth/login`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email, password }),
          signal: AbortSignal.timeout(2500)
        });
        const json = await res.json();
        if (res.ok && json.success) {
          const userObj = {
            fullName: json.data.fullName || "Amit Patil",
            email: json.data.email || email,
            role: json.data.role || "Administrative"
          };
          localStorage.setItem("crm_access_token", json.data.accessToken || "session_token");
          localStorage.setItem("crm_auth_user", JSON.stringify(userObj));
          recordAuditLog("LOGIN", userObj.email, userObj.fullName, userObj.role);
          showLoginMsg("✓ Login successful!", "ok");
          setTimeout(() => renderDashboardForUser(userObj), 300);
          return;
        } else {
          showLoginMsg(json.message || "Invalid credentials", "err");
        }
      } catch(err) {
        // Fallback for authorized staff with password "root"
        if (password === "root" && fallbackStaff[email]) {
          const user = fallbackStaff[email];
          localStorage.setItem("crm_access_token", user.accessToken);
          localStorage.setItem("crm_auth_user", JSON.stringify(user));
          recordAuditLog("LOGIN", user.email, user.fullName, user.role);
          showLoginMsg("✓ Login successful!", "ok");
          setTimeout(() => renderDashboardForUser(user), 300);
          return;
        } else {
          showLoginMsg("Invalid credentials. Default password is 'root'", "err");
        }
      } finally {
        if (btn) {
          btn.disabled = false;
          btn.textContent = "Sign In to Workspace";
        }
      }
    });
  }

  // Logout Button Action
  const logoutBtn = $("btn-do-logout");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", async () => {
      const userStr = localStorage.getItem("crm_auth_user");
      let user = null;
      if (userStr) {
        try { user = JSON.parse(userStr); } catch(e) {}
      }

      if (user) {
        recordAuditLog("LOGOUT", user.email, user.fullName, user.role);
      }

      try {
        await fetch(`${API_URL}/auth/logout`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email: user ? user.email : null })
        });
      } catch(e) {}

      localStorage.removeItem("crm_auth_user");
      localStorage.removeItem("crm_access_token");

      if ($("login-overlay")) $("login-overlay").style.display = "flex";
      if ($("crm-app-shell")) $("crm-app-shell").style.display = "none";
      showLoginMsg("You have been safely logged out.", "ok");
    });
  }

  // Audit Logs Dialog Viewer
  const logsBtn = $("btn-show-audit-logs");
  if (logsBtn) {
    logsBtn.addEventListener("click", async () => {
      const dlg = $("auth-logs-dialog");
      if (!dlg) return;
      dlg.showModal();

      const tbody = $("auth-logs-tbody");
      if (tbody) tbody.innerHTML = '<tr><td colspan="7" style="text-align:center; padding:15px; color:#8a96a8;">Loading login logs...</td></tr>';

      try {
        const res = await fetch(`${API_URL}/auth/logs`, { signal: AbortSignal.timeout(2000) });
        const json = await res.json();
        if (json.success && Array.isArray(json.data) && json.data.length > 0) {
          tbody.innerHTML = json.data.map(l => `<tr>
            <td><strong>${escapeHtml(l.fullName || "Staff")}</strong></td>
            <td>${escapeHtml(l.email)}</td>
            <td>${escapeHtml(l.roleName || "Administrative")}</td>
            <td style="color:#15803d; font-weight:600;">${l.loginTime ? new Date(l.loginTime).toLocaleTimeString([], {hour:'2-digit', minute:'2-digit', second:'2-digit'}) : "—"}</td>
            <td style="color:#b91c1c; font-weight:600;">${l.logoutTime ? new Date(l.logoutTime).toLocaleTimeString([], {hour:'2-digit', minute:'2-digit', second:'2-digit'}) : "Active"}</td>
            <td><span class="pill ${l.logoutTime ? 'status-not-interested' : 'status-completed'}">${escapeHtml(l.status || (l.logoutTime ? 'LOGGED_OUT' : 'LOGGED_IN'))}</span></td>
            <td style="color:#64748b; font-family:monospace;">${escapeHtml(l.ipAddress || "127.0.0.1")}</td>
          </tr>`).join("");
          return;
        }
      } catch(e) {}

      // Fallback from localStorage audit logs
      const localLogs = JSON.parse(localStorage.getItem("crm_audit_logs") || "[]");
      if (localLogs.length > 0) {
        tbody.innerHTML = localLogs.map(l => `<tr>
          <td><strong>${escapeHtml(l.fullName || "Amit Patil")}</strong></td>
          <td>${escapeHtml(l.email)}</td>
          <td>${escapeHtml(l.roleName || "Administrative")}</td>
          <td style="color:#15803d; font-weight:600;">${l.loginTime ? new Date(l.loginTime).toLocaleTimeString([], {hour:'2-digit', minute:'2-digit', second:'2-digit'}) : "—"}</td>
          <td style="color:${l.logoutTime ? '#b91c1c' : '#15803d'}; font-weight:600;">${l.logoutTime ? new Date(l.logoutTime).toLocaleTimeString([], {hour:'2-digit', minute:'2-digit', second:'2-digit'}) : "Active"}</td>
          <td><span class="pill ${l.logoutTime ? 'status-not-interested' : 'status-completed'}">${escapeHtml(l.status || (l.logoutTime ? 'LOGGED_OUT' : 'LOGGED_IN'))}</span></td>
          <td style="color:#64748b; font-family:monospace;">${escapeHtml(l.ipAddress || "127.0.0.1")}</td>
        </tr>`).join("");
      } else {
        tbody.innerHTML = `<tr>
          <td><strong>Amit Patil</strong></td>
          <td>amit.patil@example.com</td>
          <td>Administrative</td>
          <td style="color:#15803d; font-weight:600;">${new Date().toLocaleTimeString([], {hour:'2-digit', minute:'2-digit', second:'2-digit'})}</td>
          <td style="color:#15803d; font-weight:600;">Active Session</td>
          <td><span class="pill status-completed">LOGGED_IN</span></td>
          <td style="color:#64748b; font-family:monospace;">127.0.0.1</td>
        </tr>`;
      }
    });
  }

  // Check auth session on load
  checkAuthSession();
});

// Expose globally for inline onclick
window.fillRole = fillRole;
