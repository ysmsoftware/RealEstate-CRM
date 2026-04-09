import { Button } from "../../../components/ui/Button"
import { Table } from "../../../components/ui/Table"
import { Plus } from "lucide-react"

export default function StepBanks({ banks, setBanks, onOpenModal }) {
  return (
    <div className="space-y-4">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-2 sm:gap-4">
        <h2 className="text-xl md:text-2xl font-semibold text-gray-900">Bank Information</h2>
        <Button onClick={onOpenModal} variant="primary" size="sm" className="w-full sm:w-auto text-xs sm:text-sm">
          <Plus size={18} /> Add Bank
        </Button>
      </div>
      {banks.length > 0 ? (
        <div className="overflow-x-auto -mx-4 md:mx-0">
          <div className="inline-block min-w-full px-4 md:px-0">
            <Table
              columns={[
                { key: "bankName", label: "Bank Name" },
                { 
                  key: "branchInfo", 
                  label: "Branch & IFSC",
                  render: (_, row) => (
                    <div>
                      <div className="font-medium text-gray-900">{row.branchName}</div>
                      <div className="text-gray-500 text-xs mt-0.5">IFSC: {row.ifscCode || "N/A"}</div>
                    </div>
                  )
                },
                { 
                  key: "accountInfo", 
                  label: "A/C No & Type",
                  render: (_, row) => (
                    <div>
                      <div className="font-medium text-gray-900">{row.accountNumber}</div>
                      <div className="text-gray-500 text-xs mt-0.5">{row.accountType || "SAVINGS"}</div>
                    </div>
                  )
                },
                { 
                  key: "contactInfo", 
                  label: "Contact Detail",
                  render: (_, row) => (
                    <div>
                      <div className="font-medium text-gray-900">{row.contactPerson}</div>
                      <div className="text-gray-500 text-xs mt-0.5">{row.contactNumber}</div>
                    </div>
                  )
                },
              ]}
              data={banks}
              actions={(row) => [{ label: "Delete", onClick: () => setBanks(banks.filter((b) => b.bankDetailId !== row.bankDetailId)) }]}
            />
          </div>
        </div>
      ) : (
        <p className="text-gray-600 text-center py-8 text-sm md:text-base">No banks added yet</p>
      )}
    </div>
  )
}