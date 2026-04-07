/**
 * Centralised React Query key factory.
 *
 * Rules:
 *  - Every key is an array so React Query can do partial-match invalidation.
 *  - Keep keys hierarchical: ["entity"] → ["entity", id] → ["entity", id, "sub"]
 *  - Never hardcode strings outside this file. Always import from here.
 *
 * Usage:
 *   import { KEYS } from "@/api/keys"
 *   useQuery({ queryKey: KEYS.project(id), ... })
 *   queryClient.invalidateQueries({ queryKey: KEYS.projects() })
 */

export const KEYS = {
  // ── Dashboard ──────────────────────────────────────────────────────────────
  dashboard: () => ["dashboard"],

  // ── Projects ───────────────────────────────────────────────────────────────
  projects: () => ["projects"],
  project: (projectId) => ["projects", projectId],

  // ── Project sub-resources (scoped by projectId) ───────────────────────────
  projectEnquiries: (projectId) => ["projects", projectId, "enquiries"],
  disbursements:    (projectId) => ["projects", projectId, "disbursements"],
  amenities:        (projectId) => ["projects", projectId, "amenities"],
  banks:            (projectId) => ["projects", projectId, "banks"],
  documents:        (projectId) => ["projects", projectId, "documents"],

  // ── Enquiries ──────────────────────────────────────────────────────────────
  enquiries: () => ["enquiries"],
  enquiry:   (enquiryId) => ["enquiries", enquiryId],

  // ── Follow-ups ─────────────────────────────────────────────────────────────
  followUpTasks:    () => ["followUps", "tasks"],
  followUp:         (followUpId) => ["followUps", followUpId],
  enquiryFollowUp:  (enquiryId)  => ["followUps", "enquiry", enquiryId],
}
