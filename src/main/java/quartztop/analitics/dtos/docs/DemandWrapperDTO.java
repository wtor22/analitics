package quartztop.analitics.dtos.docs;


import java.util.List;

public class DemandWrapperDTO {
    private List<DemandDTO> rows;

    public List<DemandDTO> getRows() {
        return rows;
    }

    public void setRows(List<DemandDTO> rows) {
        this.rows = rows;
    }
}
