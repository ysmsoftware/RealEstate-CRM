import { Button } from "../../../components/ui/Button"
import { Table } from "../../../components/ui/Table"
import { Plus, AlertTriangle, CheckCircle2 } from "lucide-react"

export default function StepDisbursements({ disbursements, setDisbursements, onOpenModal }) {
  const total = disbursements.reduce((sum, d) => sum + Number.parseFloat(d.percentage), 0)
  
  return (
    <div className="space-y-4">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-2 sm:gap-4">
        <h2 className="text-xl md:text-2xl font-semibold text-gray-900">Disbursements</h2>
        <Button 
          onClick={onOpenModal} 
          variant="primary" 
          size="sm" 
          disabled={total >= 100}
          className="w-full sm:w-auto text-xs sm:text-sm disabled:opacity-50"
        >
          <Plus size={18} /> Add Disbursement
        </Button>
      </div>

      <div className={`p-4 border rounded-lg flex items-start gap-3 ${
          total === 100 
            ? "bg-green-50 border-green-200 text-green-800" 
            : "bg-amber-50 border-amber-200 text-amber-800"
        }`}
      >
        {total === 100 ? (
          <CheckCircle2 className="shrink-0 mt-0.5 text-green-600" size={20} />
        ) : (
          <AlertTriangle className="shrink-0 mt-0.5 text-amber-600" size={20} />
        )}
        <div>
          <p className="font-semibold text-sm md:text-base">
            Total Percentage: {total}% / 100%
          </p>
          <p className="text-sm mt-1">
            {total === 100 
              ? "The disbursement schedule is complete." 
              : `You must allocate exactly 100%. Please add ${100 - total}% more to proceed.`}
          </p>
        </div>
      </div>

      {disbursements.length > 0 ? (
        <div className="overflow-x-auto -mx-4 md:mx-0">
          <div className="inline-block min-w-full px-4 md:px-0">
            <Table
              columns={[{ key: "title", label: "Title" }, { key: "description", label: "Description" }, { key: "percentage", label: "Percentage" }]}
              data={disbursements.map(d => ({...d, percentage: `${d.percentage}%`}))}
              actions={(row) => [{ label: "Delete", onClick: () => setDisbursements(disbursements.filter((d) => d.disbursementId !== row.disbursementId)) }]}
            />
          </div>
        </div>
      ) : (
        <p className="text-gray-500 text-center py-8 text-sm md:text-base bg-gray-50 rounded border border-dashed border-gray-200">
          No disbursements added yet. You need to assign the disbursement milestones till it sums up to exactly 100%.
        </p>
      )}
    </div>
  )
}