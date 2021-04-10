package ru.alex2772.vcpkggui.model;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public abstract class PackageTableModel extends AbstractTableModel {
    private final List<VcpkgPackage> mPackages;

    public PackageTableModel(List<VcpkgPackage> packages) {
        mPackages = packages;
    }

    @Override
    public int getRowCount() {
        return mPackages.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }


    public VcpkgPackage getPackage(int index) {
        return mPackages.get(index);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        VcpkgPackage item = mPackages.get(rowIndex);
        switch (columnIndex) {
            case 0: return item.getName();
            case 1: return item.getVersion();
            case 2: return getPlatform(item);
            case 3: return item.getDescription();
        }
        return null;
    }

    protected abstract Object getPlatform(VcpkgPackage item);

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0: return "Name";
            case 1: return "Version";
            case 2: return "Platform";
        }
        return super.getColumnName(column);
    }
}
