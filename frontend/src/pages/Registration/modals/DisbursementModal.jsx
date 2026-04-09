import { Modal } from "../../../components/ui/Modal"
import { Button } from "../../../components/ui/Button"
import { FormInput } from "../../../components/ui/FormInput"
import { AlertCircle } from "lucide-react"

export default function DisbursementModal({ isOpen, onClose, form, setForm, onAdd, currentDisbursements = [] }) {
  const currentTotal = currentDisbursements.reduce((sum, d) => sum + Number.parseFloat(d.percentage || 0), 0)
  const newPercentage = Number.parseFloat(form.percentage) || 0

  let isError = false
  let errorMsg = ""

  if (!form.title || !form.percentage) {
    isError = true
    errorMsg = "Please fill all fields"
  } else if (newPercentage <= 0) {
    isError = true
    errorMsg = "Percentage must be greater than 0"
  } else if (currentTotal + newPercentage > 100) {
    isError = true
    errorMsg = `Total cannot exceed 100%. Current total is ${currentTotal}%. You can add up to ${100 - currentTotal}%.`
  }

  const handlePercentageChange = (e) => {
    let val = e.target.value
    if (val.includes("-") || val.includes("e") || val.includes("E")) return
    setForm({ ...form, percentage: val })
  }

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Add Disbursement">
      <FormInput label="Title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} />
      <FormInput label="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
      <FormInput label="Percentage" type="number" min="0" max={100 - currentTotal} value={form.percentage} onChange={handlePercentageChange} />
      
      {isError && (
        <div className="mt-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg text-sm flex items-start gap-2">
            <AlertCircle size={18} className="text-red-500 mt-0.5 shrink-0" />
            <div>
                <span className="font-semibold block">Validation Error</span>
                <span className="text-red-600">{errorMsg}</span>
            </div>
        </div>
      )}

      <div className="flex flex-col-reverse sm:flex-row gap-2 justify-end mt-4">
        <Button onClick={onClose} variant="secondary" className="w-full sm:w-auto">Cancel</Button>
        <Button 
          onClick={onAdd} 
          disabled={isError} 
          variant="primary" 
          className="w-full sm:w-auto disabled:opacity-50 disabled:cursor-not-allowed"
        >
          Add Disbursement
        </Button>
      </div>
    </Modal>
  )
}