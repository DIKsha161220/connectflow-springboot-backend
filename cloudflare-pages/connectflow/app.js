// ConnectFlow Backend API
const API_BASE_URL = window.location.hostname === "localhost" ? "http://localhost:8080" : "https://connectflow-springboot-backend.onrender.com";

async function loadDashboardSummary() {
  try {
    const token = localStorage.getItem("crm_access_token") || "";

    const response = await fetch(
      `${API_BASE_URL}/api/dashboard/summary`,
      {
        method: "GET",
        headers: {
          "Authorization": token ? `Bearer ${token}` : "",
          "Content-Type": "application/json"
        }
      }
    );

    if (!response.ok) {
      throw new Error(`API request failed: ${response.status}`);
    }

    const data = await response.json();

    if ($("stat-customers")) $("stat-customers").textContent = Math.max(visibleCustomers().length, data.totalInstitutes ?? 0);
    if ($("stat-visits")) $("stat-visits").textContent = Math.max(state.visits.length, data.totalVisits ?? 0);
    if ($("stat-followups")) $("stat-followups").textContent = Math.max(state.followups.length, data.totalFollowUps ?? 0);

    console.log("Dashboard summary loaded:", data);
  } catch (error) {
    console.warn("Dashboard API offline, using local state:", error);
  }
}



const $ = (id) => document.getElementById(id);

const roles = {
  admin: {label:"Administrative", user:"Admin User", initials:"AD", permissions:["dashboard","customers","calls","visits","followups"], canAddInstitute:true, canCall:true, canVisit:true, scope:"all"},
  calling: {label:"Calling Team", user:"Neha Shah", initials:"NS", permissions:["dashboard","customers","calls","visits","followups"], canAddInstitute:true, canCall:true, canVisit:true, scope:"all"},
  field: {label:"On-field Employee", user:"Rohit More", initials:"RM", permissions:["dashboard","customers","calls","visits","followups"], canAddInstitute:true, canCall:true, canVisit:true, scope:"all"}
};
let currentRole = "admin";
function activeRole(){ return roles[currentRole] || roles.admin; }
function visibleCustomers(){ return state.customers || []; }
function visibleNames(){ return new Set((state.customers || []).map(c=>c.name)); }
function canAccessPage(page){ return true; }
function applyRole(){
  const r=activeRole();
  const storedUser = JSON.parse(localStorage.getItem("crm_auth_user") || "{}");
  const userName = storedUser.fullName || r.user;
  const userRole = storedUser.role || r.label;
  const initials = userName.trim().split(/\s+/).filter(Boolean).map(x=>x[0]).join("").toUpperCase().substring(0,2) || "US";

  $("current-user-name").textContent = userName;
  $("current-user-role").textContent = userRole;
  $("current-user-avatar").textContent = initials;
  const sideAvatar = document.querySelector(".sidebar-bottom .avatar");
  if (sideAvatar) sideAvatar.textContent = initials;
  
  $("role-banner").textContent = `${userRole} Access: Full access to all institutes, call logs, visits and follow-ups.`;
  document.querySelectorAll(".nav-link").forEach(b=>{
    b.classList.remove("hidden");
  });
  $("primary-action").classList.remove("hidden");
  setPage(state.page || "dashboard");
}
function requirePermission(type){
  return true;
}
const state = {
  page: "dashboard",
  // DEMO ONLY: all contact names, phone numbers, addresses and activity below are illustrative.
  customers: [
    {
        "id": 80,
        "name": "infosys private limited",
        "phone": "",
        "company": "infosys private limited",
        "contacts": "interested in todays prasentation | Contact: sunil patil 9876543210",
        "street": "presentation class",
        "village": "shivajinagr",
        "taluka": "haveli",
        "district": "pune",
        "state": "Maharashtra",
        "pin": "411001",
        "referenceBy": "amit patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "2026-12-12T11:28",
        "notes": "interested in todays prasentation | Contact: sunil patil 9876543210"
    },
    {
        "id": 79,
        "name": "infosys private limited",
        "phone": "",
        "company": "infosys private limited",
        "contacts": "interested in todays prasentation | Contact: sunil patil 9876543210",
        "street": "presentation class",
        "village": "shivajinagr",
        "taluka": "haveli",
        "district": "pune",
        "state": "Maharashtra",
        "pin": "411001",
        "referenceBy": "amit patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "2026-12-12T11:28",
        "notes": "interested in todays prasentation | Contact: sunil patil 9876543210"
    },
    {
        "id": 78,
        "name": "aadvik tech private limited",
        "phone": "",
        "company": "aadvik tech private limited",
        "contacts": "interested in company demo session | Contact: sunil patil 9876543210",
        "street": "comapany  demo session",
        "village": "shivajinagr",
        "taluka": "haveli",
        "district": "pune",
        "state": "Maharashtra",
        "pin": "411001",
        "referenceBy": "amit patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "2026-11-22T22:44",
        "notes": "interested in company demo session | Contact: sunil patil 9876543210"
    },
    {
        "id": 77,
        "name": "aadvik tech private limited",
        "phone": "",
        "company": "aadvik tech private limited",
        "contacts": "interested in company demo session | Contact: sunil patil 9876543210",
        "street": "comapany  demo session",
        "village": "shivajinagr",
        "taluka": "haveli",
        "district": "pune",
        "state": "Maharashtra",
        "pin": "411001",
        "referenceBy": "amit patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "2026-11-22T22:44",
        "notes": "interested in company demo session | Contact: sunil patil 9876543210"
    },
    {
        "id": 76,
        "name": "DY.PATIL CLG ENGINNERING",
        "phone": "",
        "company": "DY.PATIL CLG ENGINNERING",
        "contacts": "Interested in demo session | Contact: Sunil Patil 9876543210",
        "street": "Sector 29, Nigdi Pradhikaran, Akurdi",
        "village": "Akurdi",
        "taluka": "Haveli",
        "district": "Pune",
        "state": "Maharashtra",
        "pin": "411044",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "2026-11-23T03:59",
        "notes": "Interested in demo session | Contact: Sunil Patil 9876543210"
    },
    {
        "id": 75,
        "name": "purnmalll lahoti clg od enginnering",
        "phone": "",
        "company": "purnmalll lahoti clg od enginnering",
        "contacts": "intereted in demo class | Contact: sunil patil 98",
        "street": "clg demo session",
        "village": "shivajinagr",
        "taluka": "haveli",
        "district": "pune",
        "state": "Maharashtra",
        "pin": "411001",
        "referenceBy": "amit patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "2026-11-23T06:30",
        "notes": "intereted in demo class | Contact: sunil patil 98"
    },
    {
        "id": 74,
        "name": "purnmalll lahoti clg od enginnering",
        "phone": "",
        "company": "purnmalll lahoti clg od enginnering",
        "contacts": "intereted in demo class | Contact: sunil patil 98",
        "street": "clg demo session",
        "village": "shivajinagr",
        "taluka": "haveli",
        "district": "pune",
        "state": "Maharashtra",
        "pin": "411001",
        "referenceBy": "amit patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "2026-11-23T06:30",
        "notes": "intereted in demo class | Contact: sunil patil 98"
    },
    {
        "id": 73,
        "name": "puranmal lahoti govern clg",
        "phone": "",
        "company": "puranmal lahoti govern clg",
        "contacts": "interested in demo | Contact: sunil patil 9876543210",
        "street": "demo for students",
        "village": "shivajinagar",
        "taluka": "haveli",
        "district": "pune",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "2026-11-23T05:58",
        "notes": "interested in demo | Contact: sunil patil 9876543210"
    },
    {
        "id": 72,
        "name": "puranmal lahoti govern clg",
        "phone": "",
        "company": "puranmal lahoti govern clg",
        "contacts": "interested in demo | Contact: sunil patil 9876543210",
        "street": "demo for students",
        "village": "shivajinagar",
        "taluka": "haveli",
        "district": "pune",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "2026-11-23T05:58",
        "notes": "interested in demo | Contact: sunil patil 9876543210"
    },
    {
        "id": 71,
        "name": "M.B.E.Society Clg of Enginnering",
        "phone": "",
        "company": "M.B.E.Society Clg of Enginnering",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "demo for class",
        "village": "shivajinagar",
        "taluka": "haveli",
        "district": "pune",
        "state": "Maharashtra",
        "pin": "411001",
        "referenceBy": "amit patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": "interested"
    },
    {
        "id": 70,
        "name": "Test Institute",
        "phone": "",
        "company": "Test Institute",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "Medium",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 69,
        "name": "Saraswati Vidya Mandir & Junior College",
        "phone": "",
        "company": "Saraswati Vidya Mandir & Junior College",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "Plot No 12, Paud Road",
        "village": "Kothrud",
        "taluka": "Haveli",
        "district": "Pune",
        "state": "Maharashtra",
        "pin": "411001",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 68,
        "name": "Dr. D. Y. Patil Institute of Tech",
        "phone": "",
        "company": "Dr. D. Y. Patil Institute of Tech",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 67,
        "name": "Indira College of Engineering",
        "phone": "",
        "company": "Indira College of Engineering",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 66,
        "name": "JSPM Rajarshi Shahu College",
        "phone": "",
        "company": "JSPM Rajarshi Shahu College",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 65,
        "name": "AISSMS College of Engineering",
        "phone": "",
        "company": "AISSMS College of Engineering",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 64,
        "name": "Cummins College of Engineering",
        "phone": "",
        "company": "Cummins College of Engineering",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 63,
        "name": "Pimpri Chinchwad College of Engg",
        "phone": "",
        "company": "Pimpri Chinchwad College of Engg",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 62,
        "name": "Sinhgad College of Engineering",
        "phone": "",
        "company": "Sinhgad College of Engineering",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 61,
        "name": "Bharati Vidyapeeth Deemed University",
        "phone": "",
        "company": "Bharati Vidyapeeth Deemed University",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 60,
        "name": "Vishwakarma Institute of Technology",
        "phone": "",
        "company": "Vishwakarma Institute of Technology",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 59,
        "name": "MIT World Peace University",
        "phone": "",
        "company": "MIT World Peace University",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 58,
        "name": "COEP Technological University",
        "phone": "",
        "company": "COEP Technological University",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 57,
        "name": "Shree Ganesh Multistate Credit Co-Op",
        "phone": "",
        "company": "Shree Ganesh Multistate Credit Co-Op",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 56,
        "name": "Modern College of Engineering, Pune",
        "phone": "",
        "company": "Modern College of Engineering, Pune",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 55,
        "name": "NEW HIGH CLG",
        "phone": "",
        "company": "NEW HIGH CLG",
        "contacts": "interested in visiting school | Contact: SUNIL PATIL 9876543210",
        "street": "COLONY ROAD",
        "village": "PARLI",
        "taluka": "BEED",
        "district": "BEED",
        "state": "Maharashtra",
        "pin": "401515",
        "referenceBy": "amit patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "Contacted",
        "followupDate": "2026-11-22T01:38",
        "notes": "interested in visiting school | Contact: SUNIL PATIL 9876543210"
    },
    {
        "id": 54,
        "name": "new modern school",
        "phone": "",
        "company": "new modern school",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "tps colonye",
        "village": "shivajinagar",
        "taluka": "haveli",
        "district": "pune",
        "state": "Maharashtra",
        "pin": "411001",
        "referenceBy": "amit patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 53,
        "name": "shree ganesh school",
        "phone": "",
        "company": "shree ganesh school",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "parli",
        "village": "parli",
        "taluka": "",
        "district": "beed",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "amit patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "Contacted",
        "followupDate": "2026-11-22T11:00",
        "notes": "parli, parli, beed \u2022 Lead: Contacted \u2022 Assigned: Amit Patil"
    },
    {
        "id": 52,
        "name": "xyz.xyz.xyz",
        "phone": "",
        "company": "xyz.xyz.xyz",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "mnl, abc, exg",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Existing Institute",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "2026-11-23T08:34",
        "notes": "mnl, abc, exg | Lead: New"
    },
    {
        "id": 51,
        "name": "xyz.xyz.xys",
        "phone": "",
        "company": "xyz.xyz.xys",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "abc",
        "village": "abs",
        "taluka": "haveli",
        "district": "pune",
        "state": "Maharashtra",
        "pin": "411001",
        "referenceBy": "amit patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 50,
        "name": "DY Patil College of Engineering",
        "phone": "",
        "company": "DY Patil College of Engineering",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "Sector 29, Akurdi",
        "village": "shivajinagar",
        "taluka": "Haveli",
        "district": "Pune",
        "state": "Maharashtra",
        "pin": "431515",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": "Interested in CRM software demo"
    },
    {
        "id": 49,
        "name": "Modern College Pune",
        "phone": "",
        "company": "Modern College Pune",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "FC Road",
        "village": "shivajinagar",
        "taluka": "haveli",
        "district": "pune",
        "state": "Maharashtra",
        "pin": "411001",
        "referenceBy": "amit patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": "interested in demo session"
    },
    {
        "id": 48,
        "name": "Saraswati Vidya Mandir & Junior College",
        "phone": "",
        "company": "Saraswati Vidya Mandir & Junior College",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "Plot No 12, Paud Road",
        "village": "Kothrud",
        "taluka": "Haveli",
        "district": "Pune",
        "state": "Maharashtra",
        "pin": "411001",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 47,
        "name": "Test Institute",
        "phone": "",
        "company": "Test Institute",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "Medium",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 46,
        "name": "Test Institute",
        "phone": "",
        "company": "Test Institute",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "Medium",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 45,
        "name": "Test Institute",
        "phone": "",
        "company": "Test Institute",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "Medium",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 44,
        "name": "Test Institute",
        "phone": "",
        "company": "Test Institute",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "Medium",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 43,
        "name": "Test Institute",
        "phone": "",
        "company": "Test Institute",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "Medium",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 42,
        "name": "Test Institute",
        "phone": "",
        "company": "Test Institute",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "Medium",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 41,
        "name": "Test Institute",
        "phone": "",
        "company": "Test Institute",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "Medium",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 40,
        "name": "Test Institute",
        "phone": "",
        "company": "Test Institute",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "Medium",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 39,
        "name": "Test Institute",
        "phone": "",
        "company": "Test Institute",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "Medium",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 38,
        "name": "Test Institute",
        "phone": "",
        "company": "Test Institute",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "",
        "village": "",
        "taluka": "",
        "district": "",
        "state": "Maharashtra",
        "pin": "",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Employee",
        "branchCount": "1",
        "priority": "Medium",
        "owner": "Amit Patil",
        "status": "New",
        "followupDate": "",
        "notes": ""
    },
    {
        "id": 1,
        "name": "Demo Institute Updated",
        "phone": "",
        "company": "Demo Institute Updated",
        "contacts": "Sunil Patil | Principal | 9876543210",
        "street": "Sample Road 123",
        "village": "Test Village",
        "taluka": "Test Taluka",
        "district": "Pune",
        "state": "Maharashtra",
        "pin": "411005",
        "referenceBy": "Amit Patil",
        "referenceFrom": "Employee",
        "branchCount": "5",
        "priority": "High",
        "owner": "Amit Patil",
        "status": "Interested",
        "followupDate": "",
        "notes": "Updated from Postman"
    }
],
  calls: [
    {id:101,customer:"Shree Ganesh Co Op Credit Society Ltd.",type:"Outgoing",date:"16 Oct, 11:20",result:"Interested",notes:"DEMO: Asked for product demo."},
    {id:102,customer:"Shree Nagari Sahakari Patsanstha Maryadit",type:"Outgoing",date:"16 Oct, 15:10",result:"Call Back",notes:"DEMO: Call after committee meeting."},
    {id:103,customer:"Ganesh Chits Fund Pvt Ltd.",type:"Incoming",date:"15 Oct, 12:40",result:"Interested",notes:"DEMO: Requested brochure by email."},
    {id:104,customer:"Aadhar Patpedhi Maryadit",type:"Outgoing",date:"14 Oct, 10:15",result:"No Answer",notes:"DEMO: Try again in the afternoon."}
  ],
  visits: [
    {id:201,customer:"Shree Ganesh Co Op Credit Society Ltd.",date:"2026-10-19",owner:"Amit Patil",status:"Scheduled",purpose:"Software demo",notes:"DEMO: Bring laptop and demo credentials."},
    {id:202,customer:"Ganesh Chits Fund Pvt Ltd.",date:"2026-10-15",owner:"Rohit More",status:"Completed",purpose:"Introductory meeting",notes:"DEMO: Shared product overview."},
    {id:203,customer:"Ganesha Multistate Credit Co Op Society Ltd.",date:"2026-10-21",owner:"Neha Shah",status:"Scheduled",purpose:"Requirements discussion",notes:"DEMO: Discuss multi-branch needs."}
  ],
  followups: [
    {
        "id": 80001,
        "customer": "infosys private limited",
        "date": "2026-12-12T11:28",
        "owner": "Amit Patil",
        "status": "Upcoming",
        "notes": "interested in todays prasentation | Contact: sunil patil 9876543210"
    },
    {
        "id": 78001,
        "customer": "aadvik tech private limited",
        "date": "2026-11-22T22:44",
        "owner": "Amit Patil",
        "status": "Upcoming",
        "notes": "interested in company demo session | Contact: sunil patil 9876543210"
    },
    {
        "id": 76001,
        "customer": "DY.PATIL CLG ENGINNERING",
        "date": "2026-11-23T03:59",
        "owner": "Amit Patil",
        "status": "Upcoming",
        "notes": "Interested in demo session | Contact: Sunil Patil 9876543210"
    },
    {
        "id": 75001,
        "customer": "purnmalll lahoti clg od enginnering",
        "date": "2026-11-23T06:30",
        "owner": "Amit Patil",
        "status": "Upcoming",
        "notes": "intereted in demo class | Contact: sunil patil 98"
    },
    {
        "id": 73001,
        "customer": "puranmal lahoti govern clg",
        "date": "2026-11-23T05:58",
        "owner": "Amit Patil",
        "status": "Upcoming",
        "notes": "interested in demo | Contact: sunil patil 9876543210"
    },
    {
        "id": 55001,
        "customer": "NEW HIGH CLG",
        "date": "2026-11-22T01:38",
        "owner": "Amit Patil",
        "status": "Upcoming",
        "notes": "interested in visiting school | Contact: SUNIL PATIL 9876543210"
    },
    {
        "id": 53001,
        "customer": "shree ganesh school",
        "date": "2026-11-22T11:00",
        "owner": "Amit Patil",
        "status": "Upcoming",
        "notes": "parli, parli, beed \u2022 Lead: Contacted \u2022 Assigned: Amit Patil"
    },
    {
        "id": 52001,
        "customer": "xyz.xyz.xyz",
        "date": "2026-11-23T08:34",
        "owner": "Amit Patil",
        "status": "Upcoming",
        "notes": "mnl, abc, exg | Lead: New"
    }
],
  nextId: 400
};

function loadStoredState() {
  try {
    const c = localStorage.getItem("crm_saved_customers");
    if (c) {
      const parsed = JSON.parse(c);
      if (Array.isArray(parsed) && parsed.length > 0) {
        // Merge: keep all 39 latest institutes, and append any custom items added in browser
        const stateNames = new Set(state.customers.map(x => (x.name || "").toLowerCase()));
        const customItems = parsed.filter(x => !stateNames.has((x.name || "").toLowerCase()));
        state.customers = [...state.customers, ...customItems];
      }
    }
    const cl = localStorage.getItem("crm_saved_calls");
    if (cl) {
      const parsed = JSON.parse(cl);
      if (Array.isArray(parsed)) state.calls = parsed;
    }
    const v = localStorage.getItem("crm_saved_visits");
    if (v) {
      const parsed = JSON.parse(v);
      if (Array.isArray(parsed)) state.visits = parsed;
    }
    const f = localStorage.getItem("crm_saved_followups");
    if (f) {
      const parsed = JSON.parse(f);
      if (Array.isArray(parsed) && parsed.length > 0) {
        const existingKeys = new Set(state.followups.map(x => `${(x.customer||'').toLowerCase()}|${x.date}`));
        const customFollowups = parsed.filter(x => !existingKeys.has(`${(x.customer||'').toLowerCase()}|${x.date}`));
        state.followups = [...state.followups, ...customFollowups];
      }
    }
  } catch(e) {}
  persistState();
}

function persistState() {
  try {
    localStorage.setItem("crm_saved_customers", JSON.stringify(state.customers));
    localStorage.setItem("crm_saved_calls", JSON.stringify(state.calls));
    localStorage.setItem("crm_saved_visits", JSON.stringify(state.visits));
    localStorage.setItem("crm_saved_followups", JSON.stringify(state.followups));
  } catch(e) {}
}

loadStoredState();

const pageInfo = {
  dashboard: ["Dashboard", "Here’s what’s happening with your team today.", "Add Institute"],
  customers: ["Customers", "Manage institute information and leads.", "Add Institute"],
  calls: ["Call Logs", "Track calls and their outcomes.", "Log Call"],
  visits: ["Visits", "Plan and record customer visits.", "Record Visit"],
  followups: ["Follow-ups", "Never miss your next customer follow-up.", "Schedule Follow-up"]
};

const storedUserObj = JSON.parse(localStorage.getItem("crm_auth_user") || "{}");
const activeCurrentUserName = storedUserObj.fullName || "Rahul Shinde";
const employeeOptions = Array.from(new Set(["Unassigned", activeCurrentUserName, "Amit Patil", "Neha Shah", "Rohit More"]));
const statusOptions = ["New", "Contacted", "Interested", "Follow-up", "Converted", "Not Interested"];
const priorityOptions = ["High", "Medium", "Low"];
const callResults = ["Interested", "Call Back", "No Answer", "Not Interested"];
const visitStatuses = ["Scheduled", "Completed", "Cancelled"];

function escapeHtml(value="") {
  return String(value).replace(/[&<>"']/g, c => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
}
function initials(name="") { return name.split(/\s+/).filter(Boolean).slice(0,2).map(x=>x[0]).join("").toUpperCase(); }
function statusClass(status="") { return "status-" + status.toLowerCase().replace(/\s+/g,"-"); }
function badge(status) { return `<span class="status ${statusClass(status)}">${escapeHtml(status)}</span>`; }
function formatDate(date) {
  if (!date) return "—";
  const d = new Date(date);
  if (Number.isNaN(d.getTime())) return date;
  return d.toLocaleDateString("en-IN",{day:"2-digit",month:"short",year:"numeric"});
}
function formatDateTime(date) {
  if (!date) return "—";
  const d = new Date(date);
  if (Number.isNaN(d.getTime())) return date;
  return d.toLocaleDateString("en-IN",{day:"2-digit",month:"short"}) + " · " + d.toLocaleTimeString("en-IN",{hour:"2-digit",minute:"2-digit"});
}
function showNotice(message) {
  $("notice").textContent = message;
  $("notice").classList.remove("hidden");
  window.clearTimeout(showNotice.timer);
  showNotice.timer = window.setTimeout(() => $("notice").classList.add("hidden"), 4200);
}
function setPage(page) {
  if (!pageInfo[page]) return;
  state.page = page;
  document.querySelectorAll(".nav-link").forEach(b=>b.classList.toggle("active",b.dataset.page===page));
  document.querySelectorAll(".page-panel").forEach(p=>p.classList.add("hidden"));
  $(page+"-page").classList.remove("hidden");
  $("page-title").textContent = pageInfo[page][0];
  $("breadcrumb-page").textContent = pageInfo[page][0];
  $("page-subtitle").textContent = pageInfo[page][1];
  $("primary-action-label").textContent = pageInfo[page][2];
  $("primary-action").classList.toggle("hidden", false);
  $("sidebar").classList.remove("open");
  renderAll();
}
function openDialog(type) {
  const titleMap = {customer:"Add Institute",call:"Log a Call",visit:"Record Customer Visit",followup:"Schedule Follow-up"};
  $("dialog-title").textContent = titleMap[type];
  $("dialog-fields").innerHTML = fieldsFor(type);
  $("record-dialog").dataset.type = type;
  $("record-dialog").showModal();
}
function field(label,name,kind="text",opts=[],full=false,required=false,placeholder="") {
  let control = "";
  if(kind==="select") {
    control = `<select name="${name}" ${required?"required":""}>${opts.map(o=>`<option value="${escapeHtml(o.value??o)}">${escapeHtml(o.label??o)}</option>`).join("")}</select>`;
  } else if(kind==="textarea") {
    control = `<textarea name="${name}" placeholder="${escapeHtml(placeholder)}"></textarea>`;
  } else {
    const extraAttrs = name === "branchCount" ? ' min="1" step="1" inputmode="numeric"' : (name === "pin" ? ' maxlength="6" inputmode="numeric"' : "");
    control = `<input name="${name}" type="${kind}"${extraAttrs} ${required?"required":""} placeholder="${escapeHtml(placeholder)}">`;
  }
  return `<div class="form-field ${full?"full-width":""}"><label for="field-${name}">${escapeHtml(label)}${required?" *":""}</label>${control}</div>`;
}
function fieldsFor(type) {
  const customerNames = visibleCustomers().map(c=>({value:c.name,label:c.name}));
  if(type==="customer") return [
    field("Institute Name","name","text",[],true,true,"e.g. Shree Ganesh Co Op Credit Society Ltd."),
    field("Street / Area","street","text",[],true,false,"e.g. Demo Market Road"),
    field("Gaon / Village","village","text",[],false,false,"e.g. Shivajinagar"),
    field("Taluka","taluka","text",[],false,false,"e.g. Haveli"),
    field("District","district","text",[],false,false,"e.g. Pune"),
    field("State","state","text",[],false,false,"e.g. Maharashtra"),
    field("PIN Code","pin","text",[],false,false,"e.g. 411005 (6 digits)"),
    field("Contact Person(s)","contacts","textarea",[],true,false,"One person per line: Name | Position | Contact No.\nExample: Sunil Patil | Principal | 9876543210"),
    field("Reference By","referenceBy","text",[],false,false,"e.g. Amit Patil"),
    field("Reference From","referenceFrom","select",["Select source","Employee","Existing Institute","Website","WhatsApp","Other"].map(x=>({value:x==="Select source"?"":x,label:x}))),
    field("No. of Branches","branchCount","number",[],false,false,"e.g. 5"),
    field("Priority","priority","select",priorityOptions.map(x=>({value:x,label:x}))),
    field("Assigned Employee","owner","select",employeeOptions.map(x=>({value:x,label:x})),false,true),
    field("Lead Status","status","select",statusOptions.map(x=>({value:x,label:x})),false,true),
    field("Next Follow-up Date & Time","followupDate","datetime-local",[],true),
    field("Notes","notes","textarea",[],true,false,"e.g. Interested in software demo; asked for quotation")
  ].join("");
  if(type==="call") return [
    field("Customer","customer","select",customerNames.map(x=>({value:x.value,label:x.label})),true,true),
    field("Call type","callType","select",["Outgoing","Incoming"].map(x=>({value:x,label:x}))),
    field("Call date & time","activityDate","datetime-local",[],false,true),
    field("Employee who called","owner","select",employeeOptions.map(x=>({value:x,label:x})),false,true),
    field("Contact person called","contactPerson","text",[],false,false,"e.g. Sunil Patil"),
    field("Contact number","contactPhone","tel",[],false,false,"e.g. 9876543210"),
    field("Call outcome","result","select",callResults.map(x=>({value:x,label:x})),false,true),
    field("Next follow-up date & time","followupDate","datetime-local"),
    field("Call notes","notes","textarea",[],true,false,"What was discussed? What happens next?")
  ].join("");
  if(type==="visit") return [
    field("Customer / company","customer","select",customerNames.map(x=>({value:state.customers.find(c=>c.name===x.value)?.company || x.value,label:`${x.value} — ${state.customers.find(c=>c.name===x.value)?.company || ""}`})),true,true),
    field("Visit date & time","activityDate","datetime-local",[],false,true),
    field("Contact person met","contactPerson","text",[],false,false,"e.g. Sunil Patil"),
    field("Contact number","contactPhone","tel",[],false,false,"e.g. 9876543210"),
    field("Assigned employee","owner","select",employeeOptions.map(x=>({value:x,label:x}))),
    field("Visit status","status","select",visitStatuses.map(x=>({value:x,label:x}))),
    field("Purpose","purpose","text",[],true,false,"Purpose of visit"),
    field("Visit notes","notes","textarea",[],true,false,"Visit outcome and next steps")
  ].join("");
  return [
    field("Customer","customer","select",customerNames.map(x=>({value:x.value,label:x.label})),true,true),
    field("Follow-up date & time","date","datetime-local",[],true,true),
    field("Assigned employee","owner","select",employeeOptions.map(x=>({value:x,label:x}))),
    field("Notes","notes","textarea",[],true,false,"What needs to be discussed?")
  ].join("");
}
function customerByName(name) { return state.customers.find(c=>c.name===name); }
function renderCustomers() {
  const q = ($("customer-search").value||"").toLowerCase();
  const status = $("customer-status-filter").value;
  const rows = visibleCustomers().filter(c=>(`${c.name} ${c.phone} ${c.owner} ${c.district||""} ${c.state||""} ${c.village||""} ${c.street||""} ${c.contacts||""} ${c.referenceBy||""} ${c.referenceFrom||""} ${c.branchCount||""}`).toLowerCase().includes(q)&&(!status||c.status===status));
  $("customer-record-count").textContent = `${rows.length} record${rows.length===1?"":"s"}`;
  $("customers-table").innerHTML = rows.map(c=>`<tr>
    <td><div class="customer-cell"><div class="mini-avatar">${escapeHtml(initials(c.name))}</div><div><button class="institute-link" data-open-institute="${escapeHtml(c.name)}">${escapeHtml(c.name)}</button><small>${escapeHtml(c.contacts||"No contact person added")}</small></div></div></td>
    <td>${escapeHtml([c.village,c.taluka,c.district,c.state].filter(Boolean).join(", ")||c.street||"—")}</td>
    <td>${escapeHtml(c.phone||"—")}</td><td>${escapeHtml(c.branchCount||"—")}</td><td>${escapeHtml(c.owner||"Unassigned")}</td><td>${badge(c.priority||"Medium")}</td><td>${badge(c.status)}</td>
    <td>${escapeHtml(c.followupDate?formatDateTime(c.followupDate):"—")}</td>
    <td><button class="table-action" data-log-customer="${escapeHtml(c.name)}">Log call</button></td></tr>`).join("") || `<tr><td colspan="9"><div class="empty-state">No institutes found.</div></td></tr>`;
  $("customers-mobile").innerHTML = rows.map(c=>`<div class="mobile-record"><div class="mobile-record-top"><div class="mini-avatar">${escapeHtml(initials(c.name))}</div><div><button class="institute-link" data-open-institute="${escapeHtml(c.name)}">${escapeHtml(c.name)}</button><small>${escapeHtml([c.village,c.taluka,c.district,c.state].filter(Boolean).join(", ")||c.street||"Address not added")}</small></div>${badge(c.status)}</div><div class="mobile-record-meta"><span>☎ ${escapeHtml(c.phone||"No contact no.")}</span><span>${escapeHtml(c.owner||"Unassigned")}</span><span>Branches: ${escapeHtml(c.branchCount||"Not entered")}</span><span>Priority: ${escapeHtml(c.priority||"Medium")}</span><span>Next: ${escapeHtml(c.followupDate?formatDateTime(c.followupDate):"Not scheduled")}</span></div><div class="mobile-record-actions"><button class="table-action" data-log-customer="${escapeHtml(c.name)}">Log call</button></div></div>`).join("") || `<div class="empty-state">No institutes found.</div>`;
}
function renderCalls() {
  const q=($("call-search").value||"").toLowerCase(), result=$("call-result-filter").value;
  const rows=state.calls.filter(c=>visibleNames().has(c.customer)&&(`${c.customer} ${c.result} ${c.notes} ${c.date}`).toLowerCase().includes(q)&&(!result||c.result===result));
  $("call-record-count").textContent=`${rows.length} log${rows.length===1?"":"s"}`;
  $("calls-table").innerHTML=rows.map(c=>`<tr><td><div class="customer-cell"><div class="mini-avatar">${escapeHtml(initials(c.customer))}</div><div><button class="institute-link" data-open-institute="${escapeHtml(c.customer)}">${escapeHtml(c.customer)}</button></div></div></td><td>${escapeHtml(c.type)}</td><td>${escapeHtml(c.date)}</td><td>${badge(c.result)}</td><td>${escapeHtml(c.notes||"—")}</td></tr>`).join("")||`<tr><td colspan="5"><div class="empty-state">No call logs found.</div></td></tr>`;
  $("calls-mobile").innerHTML=rows.map(c=>`<div class="mobile-record"><div class="mobile-record-top"><div class="mini-avatar">☎</div><div><button class="institute-link" data-open-institute="${escapeHtml(c.customer)}">${escapeHtml(c.customer)}</button><small>${escapeHtml(c.date)} · ${escapeHtml(c.type)}</small></div>${badge(c.result)}</div><div class="mobile-record-meta"><span>${escapeHtml(c.notes||"No notes")}</span></div></div>`).join("")||`<div class="empty-state">No call logs found.</div>`;
}
function renderVisits() {
  const q=($("visit-search").value||"").toLowerCase(), status=$("visit-status-filter").value;
  const rows=state.visits.filter(v=>visibleNames().has(v.customer)&&(`${v.customer} ${v.owner} ${v.purpose}`).toLowerCase().includes(q)&&(!status||v.status===status));
  $("visit-record-count").textContent=`${rows.length} visit${rows.length===1?"":"s"}`;
  $("visits-table").innerHTML=rows.map(v=>`<tr><td><div class="customer-cell"><div class="mini-avatar">⌖</div><div><button class="institute-link" data-open-institute="${escapeHtml(v.customer)}">${escapeHtml(v.customer)}</button><small>${escapeHtml(v.purpose||"Customer visit")}</small></div></div></td><td>${escapeHtml(formatDate(v.date))}</td><td>${escapeHtml(v.owner)}</td><td>${badge(v.status)}</td><td>${v.status==="Scheduled"?`<button class="table-action" data-complete-visit="${v.id}">Mark completed</button>`:"—"}</td></tr>`).join("")||`<tr><td colspan="5"><div class="empty-state">No visit records found.</div></td></tr>`;
  $("visits-mobile").innerHTML=rows.map(v=>`<div class="mobile-record"><div class="mobile-record-top"><div class="mini-avatar">⌖</div><div><button class="institute-link" data-open-institute="${escapeHtml(v.customer)}">${escapeHtml(v.customer)}</button><small>${escapeHtml(formatDate(v.date))}</small></div>${badge(v.status)}</div><div class="mobile-record-meta"><span>${escapeHtml(v.owner)}</span><span>${escapeHtml(v.purpose||"Customer visit")}</span></div>${v.status==="Scheduled"?`<div class="mobile-record-actions"><button class="table-action" data-complete-visit="${v.id}">Mark completed</button></div>`:""}</div>`).join("")||`<div class="empty-state">No visit records found.</div>`;
}
function renderFollowups() {
  const q=($("followup-search").value||"").toLowerCase(), filter=$("followup-filter").value;
  const rows=state.followups.filter(f=>visibleNames().has(f.customer)&&(`${f.customer} ${f.owner} ${f.notes} ${f.status}`).toLowerCase().includes(q)&&(!filter||f.status===filter));
  $("due-count").textContent=state.followups.filter(f=>visibleNames().has(f.customer)&&f.status==="Due Today").length;
  $("overdue-count").textContent=state.followups.filter(f=>visibleNames().has(f.customer)&&f.status==="Overdue").length;
  $("upcoming-count").textContent=state.followups.filter(f=>visibleNames().has(f.customer)&&f.status==="Upcoming").length;
  $("followup-count").textContent=state.followups.filter(f=>visibleNames().has(f.customer)&&(f.status==="Due Today"||f.status==="Overdue")).length;
  $("followups-list").innerHTML=rows.map(f=>`<div class="followup-card ${f.status==="Overdue"?"is-overdue":""}"><div class="followup-date-icon">◷</div><div class="followup-details"><strong><button class="institute-link" data-open-institute="${escapeHtml(f.customer)}">${escapeHtml(f.customer)}</button> ${badge(f.status)}</strong><p>${escapeHtml(formatDateTime(f.date))} · ${escapeHtml(f.owner)}</p><p>${escapeHtml(f.notes||"No notes")}</p></div><div class="followup-actions">${f.status!=="Completed"?`<button class="small-button" data-complete-followup="${f.id}">✓ Complete</button><button class="small-button" data-reschedule-followup="${f.id}">Reschedule</button><button class="small-button" data-followup-call="${escapeHtml(f.customer)}">Log call</button>`:`<span class="pill pill-blue">Done</span>`}</div></div>`).join("")||`<div class="empty-state">No follow-ups found.</div>`;
}
function renderDashboard() {
  $("stat-customers").textContent=visibleCustomers().length;
  $("stat-calls").textContent=state.calls.filter(c=>visibleNames().has(c.customer)).length;
  $("stat-visits").textContent=state.visits.filter(v=>visibleNames().has(v.customer)).length;
  $("stat-followups").textContent=state.followups.filter(f=>f.status!=="Completed").length;
  const seenCust = new Set();
  const uniquePending = [];
  for (const f of state.followups) {
    if (visibleNames().has(f.customer) && f.status !== "Completed") {
      const key = (f.customer || "").trim().toLowerCase();
      if (!seenCust.has(key)) {
        seenCust.add(key);
        uniquePending.push(f);
      }
    }
  }
  const pending = uniquePending.slice(0, 5);
  $("dashboard-followups").innerHTML=pending.map(f=>`<div class="activity-row"><div class="mini-avatar">${escapeHtml(initials(f.customer))}</div><div class="activity-main"><button class="institute-link" data-open-institute="${escapeHtml(f.customer)}">${escapeHtml(f.customer)}</button><small>${escapeHtml(formatDateTime(f.date))} · ${escapeHtml(f.owner)}</small></div><div class="activity-side">${badge(f.status)}</div></div>`).join("")||`<div class="empty-state">All follow-ups completed.</div>`;
  $("dashboard-calls").innerHTML=state.calls.filter(c=>visibleNames().has(c.customer)).slice(0,4).map(c=>`<div class="activity-row"><div class="mini-avatar">☎</div><div class="activity-main"><button class="institute-link" data-open-institute="${escapeHtml(c.customer)}">${escapeHtml(c.customer)}</button><small>${escapeHtml(c.date)} · ${escapeHtml(c.type)}</small></div><div class="activity-side">${badge(c.result)}</div></div>`).join("")||`<div class="empty-state">No calls logged yet.</div>`;
}
function activityDateLabel(value) {
  if(!value) return "—";
  const d=new Date(value);
  return Number.isNaN(d.getTime()) ? value : d.toLocaleString("en-IN",{day:"2-digit",month:"short",year:"numeric",hour:"2-digit",minute:"2-digit"});
}
function openInstitute(name) {
  const c=customerByName(name); if(!c) return;
  const calls=state.calls.filter(x=>x.customer===name).map(x=>({kind:"Call",date:x.date,employee:x.owner||"Not recorded",contact:x.contactPerson||"Not recorded",phone:x.contactPhone||c.phone||"Not recorded",summary:`${x.type||"Call"} · ${x.result||"No outcome"}${x.notes?" — "+x.notes:""}`}));
  const visits=state.visits.filter(x=>x.customer===name).map(x=>({kind:"Visit",date:x.date,employee:x.owner||"Not recorded",contact:x.contactPerson||"Not recorded",phone:x.contactPhone||c.phone||"Not recorded",summary:`${x.purpose||"Institute visit"} · ${x.status||""}${x.notes?" — "+x.notes:""}`}));
  const activity=[...calls,...visits].sort((a,b)=>new Date(b.date)-new Date(a.date));
  const address=[c.street,c.village,c.taluka,c.district,c.state,c.pin].filter(Boolean).join(", ");
  $("institute-detail-content").innerHTML=`
    <div class="detail-top"><div><p class="eyebrow">INSTITUTE PROFILE</p><h2>${escapeHtml(c.name)}</h2><p>${escapeHtml(address||"Address not added")}</p></div><div class="detail-actions"><button class="btn btn-secondary ${activeRole().canCall?"":"hidden"}" data-detail-call="${escapeHtml(c.name)}">+ Add Call</button><button class="btn btn-primary ${activeRole().canVisit?"":"hidden"}" data-detail-visit="${escapeHtml(c.name)}">+ Add Visit</button></div></div>
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
    <div class="detail-history-heading"><div><h3>Call & Visit History</h3><p>Employee, date/time, contact person, contact number and discussion notes</p></div><span class="pill pill-blue">${activity.length} records</span></div>
    <div class="detail-history">${activity.map(a=>`<article class="history-item"><div class="history-kind">${escapeHtml(a.kind)}</div><div class="history-main"><strong>${escapeHtml(activityDateLabel(a.date))} · ${escapeHtml(a.employee)}</strong><p><b>Contact:</b> ${escapeHtml(a.contact)} · ${escapeHtml(a.phone)}</p><p><b>Discussion:</b> ${escapeHtml(a.summary)}</p></div></article>`).join("")||'<div class="empty-state">No calls or visits recorded yet. Use Add Call or Add Visit above.</div>'}</div>`;
  $("institute-detail-dialog").showModal();
}
function renderAll() { renderDashboard();renderCustomers();renderCalls();renderVisits();renderFollowups(); }

function saveRecord(type, data) {
  const now = new Date();
  if(type==="customer") {
    const instName = (data.name || "").trim();
    if(!instName) {
      alert("Please enter Institute Name / संस्थेचे नाव टाका!");
      return;
    }

    const primaryPhone = (data.contacts||"").split("\n").map(line=>line.split("|").map(part=>part.trim())).find(parts=>parts[2]);
    const phone = primaryPhone?.[2] || data.phone || "";

    const customer = {
      id: state.nextId++,
      name: instName,
      phone: phone,
      company: instName,
      street: (data.street || "").trim(),
      village: (data.village || "").trim(),
      taluka: (data.taluka || "").trim(),
      district: (data.district || "").trim(),
      state: (data.state || "Maharashtra").trim(),
      pin: (data.pin || "").trim(),
      contacts: (data.contacts || "").trim(),
      referenceBy: (data.referenceBy || "Amit Patil").trim(),
      referenceFrom: data.referenceFrom || "Employee",
      branchCount: data.branchCount ? Math.max(1, parseInt(data.branchCount, 10) || 1) : 1,
      priority: data.priority || "High",
      owner: data.owner || "Amit Patil",
      status: data.status || "New",
      followupDate: data.followupDate ? data.followupDate.slice(0, 16) : "",
      notes: (data.notes || "").trim()
    };

    // Add to state immediately
    state.customers.unshift(customer);

    if(data.followupDate) {
      state.followups.unshift({
        id: Date.now(),
        customer: customer.name,
        date: data.followupDate.slice(0, 16),
        owner: data.owner || "Amit Patil",
        status: "Upcoming",
        notes: data.notes ? data.notes.trim() : "Scheduled follow-up"
      });
    }

    persistState();
    $("record-dialog").close();
    renderAll();
    showNotice(`✓ "${instName}" record saved successfully!`);

    // Background push to backend APIs
    const payload = {
      instituteName: instName,
      streetAddress: customer.street || null,
      village: customer.village || null,
      taluka: customer.taluka || null,
      district: customer.district || null,
      state: customer.state || "Maharashtra",
      pinCode: (/^\d{6}$/.test(customer.pin)) ? customer.pin : null,
      branchCount: customer.branchCount,
      referenceBy: customer.referenceBy,
      referenceFrom: customer.referenceFrom,
      leadStatus: customer.status,
      priority: customer.priority,
      nextFollowUpAt: customer.followupDate ? new Date(customer.followupDate).toISOString() : null,
      notes: customer.notes
    };

    // Try primary backend
    fetch(`${API_BASE_URL}/api/institutes`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    }).catch(() => {});

    // Also try local backend
    if (API_BASE_URL !== "http://localhost:8080") {
      fetch("http://localhost:8080/api/institutes", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      }).catch(() => {});
    }
    return;
  } else if(type==="call") {
    const activityDate=data.activityDate || now.toISOString().slice(0,16);
    state.calls.unshift({id:state.nextId++,customer:data.customer,type:data.callType,date:activityDate,owner:data.owner||"Unassigned",contactPerson:data.contactPerson.trim(),contactPhone:data.contactPhone.trim(),result:data.result,notes:data.notes.trim()});
    if(data.followupDate) state.followups.unshift({id:state.nextId++,customer:data.customer,date:data.followupDate,owner:data.owner||customerByName(data.customer)?.owner||employeeOptions[0],status:"Upcoming",notes:"Follow-up after call"});
    persistState();
    $("record-dialog").close();
    renderAll();
    showNotice("Call record saved in this browser prototype.");
    return;
  } else if(type==="visit") {
    const activityDate=data.activityDate || now.toISOString().slice(0,16);
    state.visits.unshift({id:state.nextId++,customer:data.customer,date:activityDate,owner:data.owner,status:data.status,contactPerson:data.contactPerson.trim(),contactPhone:data.contactPhone.trim(),purpose:data.purpose.trim(),notes:data.notes.trim()});
    persistState();
    $("record-dialog").close();
    renderAll();
    showNotice("Visit record saved in this browser prototype.");
    return;
  } else if(type==="followup") {
    state.followups.unshift({id:state.nextId++,customer:data.customer,date:data.date,owner:data.owner,status:"Upcoming",notes:data.notes.trim()});
    persistState();
    $("record-dialog").close();
    renderAll();
    showNotice("Follow-up scheduled in this browser prototype.");
    return;
  }
}
$("record-form").addEventListener("submit", e=>{
  e.preventDefault();
  const type=$("record-dialog").dataset.type;
  const data=Object.fromEntries(new FormData(e.currentTarget).entries());
  if(type==="customer" && !data.name?.trim()) {
    alert("Please enter Institute Name / संस्थेचे नाव टाका!");
    return;
  }
  saveRecord(type,data);
});
$("primary-action").addEventListener("click",()=>openDialog(({dashboard:"customer",customers:"customer",calls:"call",visits:"visit",followups:"followup"})[state.page]));
$("close-dialog").addEventListener("click",()=>$("record-dialog").close());
$("cancel-dialog").addEventListener("click",()=>$("record-dialog").close());
$("mobile-menu").addEventListener("click",()=>$("sidebar").classList.toggle("open"));
document.querySelectorAll(".nav-link").forEach(b=>b.addEventListener("click",()=>setPage(b.dataset.page)));
$("role-switcher").addEventListener("change",e=>{currentRole=e.target.value;applyRole();showNotice(`Demo role switched to ${activeRole().label}.`);});
document.querySelectorAll("[data-goto]").forEach(b=>b.addEventListener("click",()=>setPage(b.dataset.goto)));
["customer-search","customer-status-filter"].forEach(id=>$(id).addEventListener("input",renderCustomers));
["call-search","call-result-filter"].forEach(id=>$(id).addEventListener("input",renderCalls));
["visit-search","visit-status-filter"].forEach(id=>$(id).addEventListener("input",renderVisits));
["followup-search","followup-filter"].forEach(id=>$(id).addEventListener("input",renderFollowups));
document.addEventListener("click",e=>{
  const institute=e.target.closest("[data-open-institute]");
  if(institute){openInstitute(institute.dataset.openInstitute);return;}
  const detailCall=e.target.closest("[data-detail-call]");
  if(detailCall){const name=detailCall.dataset.detailCall;$("institute-detail-dialog").close();openDialog("call");const f=$("dialog-fields");f.querySelector('[name="customer"]').value=name;const c=customerByName(name);if(c){const parts=(c.contacts||"").split("\n")[0].split("|").map(x=>x.trim());if(f.querySelector('[name="contactPerson"]'))f.querySelector('[name="contactPerson"]').value=parts[0]||"";if(f.querySelector('[name="contactPhone"]'))f.querySelector('[name="contactPhone"]').value=parts[2]||c.phone||"";if(f.querySelector('[name="owner"]'))f.querySelector('[name="owner"]').value=c.owner||"Unassigned";}return;}
  const detailVisit=e.target.closest("[data-detail-visit]");
  if(detailVisit){const name=detailVisit.dataset.detailVisit;$("institute-detail-dialog").close();openDialog("visit");const f=$("dialog-fields");f.querySelector('[name="customer"]').value=name;const c=customerByName(name);if(c){const parts=(c.contacts||"").split("\n")[0].split("|").map(x=>x.trim());if(f.querySelector('[name="contactPerson"]'))f.querySelector('[name="contactPerson"]').value=parts[0]||"";if(f.querySelector('[name="contactPhone"]'))f.querySelector('[name="contactPhone"]').value=parts[2]||c.phone||"";if(f.querySelector('[name="owner"]'))f.querySelector('[name="owner"]').value=c.owner||"Unassigned";}return;}
  const log=e.target.closest("[data-log-customer]");
  if(log){setPage("calls");openDialog("call");const select=$('dialog-fields').querySelector('[name="customer"]');if(select)select.value=log.dataset.logCustomer;}
  const completeVisit=e.target.closest("[data-complete-visit]");
  if(completeVisit){const v=state.visits.find(x=>x.id===Number(completeVisit.dataset.completeVisit));if(v)v.status="Completed";persistState();renderAll();showNotice("Visit marked as completed.");}
  const complete=e.target.closest("[data-complete-followup]");
  if(complete){const f=state.followups.find(x=>x.id===Number(complete.dataset.completeFollowup));if(f)f.status="Completed";persistState();renderAll();showNotice("Follow-up marked as completed.");}
  const reschedule=e.target.closest("[data-reschedule-followup]");
  if(reschedule){const f=state.followups.find(x=>x.id===Number(reschedule.dataset.rescheduleFollowup));if(!f)return;openDialog("followup");const fields=$("dialog-fields");fields.querySelector('[name="customer"]').value=f.customer;fields.querySelector('[name="owner"]').value=f.owner;fields.querySelector('[name="date"]').value=f.date;fields.querySelector('[name="notes"]').value=f.notes||"";state.followups=state.followups.filter(x=>x.id!==f.id);persistState();}
  const logFollowup=e.target.closest("[data-followup-call]");
  if(logFollowup){setPage("calls");openDialog("call");const select=$('dialog-fields').querySelector('[name="customer"]');if(select)select.value=logFollowup.dataset.followupCall;}
});
const notifBtn = $("notification-button");
if (notifBtn) {
  notifBtn.addEventListener("click",()=>{setPage("followups");showNotice("Review due and overdue follow-ups here. Browser push notifications are not configured in this prototype.");});
}
const todayLbl = $("today-label");
if (todayLbl) {
  todayLbl.textContent=new Date().toLocaleDateString("en-IN",{weekday:"short",day:"2-digit",month:"short"});
}
applyRole();

async function loadLiveDatabaseData() {
  const BACKEND_URL = `${API_BASE_URL}/api`;
  try {
    const res = await fetch(`${BACKEND_URL}/institutes`);
    if (res.ok) {
      const json = await res.json();
      const list = json.data || json;
      if (Array.isArray(list) && list.length > 0) {
        const liveCustomers = list.map(inst => ({
          id: inst.instituteId || inst.id,
          name: inst.instituteName || inst.name,
          phone: inst.primaryContactPhone || inst.phone || "",
          company: inst.instituteName || inst.name,
          contacts: (inst.contacts && inst.contacts.length > 0) 
            ? inst.contacts.map(c => `${c.contactPerson} | ${c.designation || "Contact"} | ${c.phone}`).join("\n") 
            : (inst.phone || "No contacts"),
          street: inst.streetAddress || "",
          village: inst.village || "",
          taluka: inst.taluka || "",
          district: inst.district || "",
          state: inst.state || "Maharashtra",
          pin: inst.pinCode || "",
          referenceBy: inst.referenceBy || "Employee",
          referenceFrom: inst.referenceFrom || "Employee",
          branchCount: inst.branchCount || 1,
          priority: inst.priority || "Medium",
          owner: inst.assignedEmployeeName || "Amit Patil",
          status: inst.leadStatus || "New",
          followupDate: inst.nextFollowUpAt || "",
          notes: inst.notes || ""
        }));
        // Merge: keep all 39 existing institutes and union with live backend institutes
        const liveIds = new Set(liveCustomers.map(x => x.id).filter(Boolean));
        const liveNames = new Set(liveCustomers.map(x => (x.name || "").trim().toLowerCase()).filter(Boolean));
        const merged = [...liveCustomers];
        for (const existing of state.customers) {
          if (!liveIds.has(existing.id) && !liveNames.has((existing.name || "").trim().toLowerCase())) {
            merged.push(existing);
          }
        }
        merged.sort((a, b) => (b.id || 0) - (a.id || 0));
        state.customers = merged;

        // Auto-sync follow-ups from live institute records that have nextFollowUpAt
        const instituteFollowups = [];
        const seenInstFollowups = new Set();
        for (const inst of liveCustomers) {
          const key = (inst.name || "").trim().toLowerCase();
          if (inst.followupDate && !seenInstFollowups.has(key)) {
            seenInstFollowups.add(key);
            const fDate = new Date(inst.followupDate);
            const today = new Date();
            let fStatus = "Upcoming";
            if (fDate.toDateString() === today.toDateString()) {
              fStatus = "Due Today";
            } else if (fDate < today) {
              fStatus = "Overdue";
            }
            instituteFollowups.push({
              id: (inst.id || 1) * 1000 + 1,
              customer: inst.name,
              date: inst.followupDate.slice(0, 16),
              owner: inst.owner || "Amit Patil",
              status: fStatus,
              notes: inst.notes || "Scheduled follow-up"
            });
          }
        }
        state.followups = [...instituteFollowups, ...state.followups.filter(f => !seenInstFollowups.has((f.customer || "").trim().toLowerCase()))];
        // Also fetch from backend /api/follow-ups
        try {
          const fRes = await fetch(`${BACKEND_URL}/follow-ups`);
          if (fRes.ok) {
            const fJson = await fRes.json();
            const fList = fJson.data || fJson;
            if (Array.isArray(fList) && fList.length > 0) {
              fList.forEach(f => {
                const match = liveCustomers.find(c => c.id === f.instituteId);
                const cName = match ? match.name : `Institute #${f.instituteId}`;
                if (!state.followups.some(ex => ex.id === f.followUpId || (ex.customer === cName && ex.date === f.scheduledAt?.slice(0, 16)))) {
                  state.followups.unshift({
                    id: f.followUpId,
                    customer: cName,
                    date: f.scheduledAt ? f.scheduledAt.slice(0, 16) : "",
                    owner: f.employeeName || "Amit Patil",
                    status: f.followUpStatus === "Pending" ? "Upcoming" : f.followUpStatus,
                    notes: f.notes || "Follow-up"
                  });
                }
              });
            }
          }
        } catch(fe) {}

        persistState();
        renderAll();
      }
    }
  } catch(e) {}
  renderAll();
}

loadLiveDatabaseData();
setInterval(loadLiveDatabaseData, 3000);


